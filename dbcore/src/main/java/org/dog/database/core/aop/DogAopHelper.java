package org.dog.database.core.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.dog.core.entry.TccLock;
import org.dog.core.util.Pair;
import org.dog.database.core.annotation.DogDb;
import org.dog.database.core.annotation.DogTable;
import org.dog.database.core.annotation.MatchType;
import org.dog.database.core.annotation.QueryArg;
import org.dog.database.core.util.ReflectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

class DogAopHelper {

    private DogDb dogDb;

    private ProceedingJoinPoint pjp;

    public DogAopHelper(ProceedingJoinPoint pjp, DogDb db) {

        this.dogDb = db;

        this.pjp = pjp;

    }


    private List<Object> getFields(Object object) throws IllegalArgumentException, IllegalAccessException {

        return  ReflectUtil.getFields(object,DogTable.class,QueryArg.class);

    }

    private List<List<Object>> getFieldsArray(Object object) throws IllegalArgumentException, IllegalAccessException {

        List<List<Object>> argObjs = new ArrayList<>();

        if (ReflectUtil.iterable(object)) {

            Iterator iterator = ((Iterable) object).iterator();

            while (iterator.hasNext()) {

                argObjs.add(getFields(iterator.next()));

            }
        }

        return argObjs;
    }


    private List<Object> getQueryArgInParamter(Object[] rawArgs) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        List<Object> argObjs = new ArrayList<>();

        Annotation[][] annotations = ReflectUtil.getParameterAnnotations(pjp);

        for (int i = 0; i < rawArgs.length; i++) {

            for (Annotation annotation : annotations[i]) {

                if (annotation instanceof QueryArg) {

                    argObjs.add(rawArgs[i]);

                    break;
                }
            }

        }


        return argObjs;
    }

    private Method getMethodWithArgSize(int argSize, List<Method> candidate) {

        for (Method method : candidate) {

            if (method.getParameterTypes().length == argSize) {

                return method;
            }
        }

        return null;
    }



    public Pair<MatchType,Pair<Method, Object[]>> getMethodAndArgObjects(String methodName) throws NoSuchFieldException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException {

        List<Method> candidateQueryMethods = getCandidateMethods(methodName);

        List<Object> rawArgs = getQueryArgs();

        /**
         * 无参情况
         */
        if (rawArgs.size() == 0) {

            Method method = getMethodWithArgSize(0, candidateQueryMethods);

            if (method != null) {

                return new Pair<>(MatchType.NoArg,new Pair<>(method, new Object[]{}));

            } else {

                throw new NoSuchMethodException();
            }

        }


        /**
         * 直接通过ArgInParamter传递
         */
        List<Object> queryArgInParamter = getQueryArgInParamter(rawArgs.toArray());

        if (queryArgInParamter.size() != 0) {

            Method method = getMethodWithArgSize(queryArgInParamter.size(), candidateQueryMethods);

            if (method != null) {

                return new  Pair<>(MatchType.ArgInParamter,new Pair<>(method, queryArgInParamter.toArray()));

            } else {

                throw new NoSuchMethodException();
            }

        }


        /**
         * 获取 DogTable 中的参数
         */
        List<Object> queryArgsInDogTable = getFields(rawArgs.get(0));

        if (queryArgsInDogTable.size() != 0) {

            Method method = getMethodWithArgSize(queryArgsInDogTable.size(), candidateQueryMethods);

            if (method != null) {

                return new  Pair<>(MatchType.ArgInDogTable,new Pair<>(method, queryArgsInDogTable.toArray()));

            } else {

                throw new NoSuchMethodException();
            }

        }

        /**
         * 直接通过 Iterator 中的参数传递  -> 只支持一个Iterator参数
         */
        List<List<Object>> iterableObjects = getFieldsArray(rawArgs.get(0));

        if (iterableObjects.size() == 0) {

            throw new NoSuchMethodException();

        } else {

            /**
             * queryArg 个数
             */
            int argNum = iterableObjects.get(0).size();

            if (argNum == 0) {

                throw new NoSuchMethodException();

            } else {

                /**
                 * 找到个数相同的method
                 */
                Method method =  getMethodWithArgSize(argNum, candidateQueryMethods);

                if(method == null){

                    throw new NoSuchMethodException();

                }else {

                    Class<?>[] clazzs =   method.getParameterTypes();

                    if(ReflectUtil.allIterable(clazzs)){

                        List<List<Object>> ret = new ArrayList<>();

                        for(int i=0;i<argNum;i++){

                            ret.add(new ArrayList<Object>());
                        }

                        for(int j=0;j<iterableObjects.size();j++){

                            for(int i=0;i<argNum;i++){

                                ret.get(i).add(iterableObjects.get(j).get(i));

                            }
                        }

                        return new Pair<>(MatchType.Iterator,new  Pair<>(method, ret.toArray()));
                    }


                    if(ReflectUtil.noneIterable(clazzs)){

                        return new Pair<>(MatchType.IteratorMutiCall,new  Pair<>(method, iterableObjects.toArray()));
                    }
                }
            }

        }


        throw new NoSuchMethodException();
    }



    public Map<TccLock,Object[]> getLocksInMutiIterator(Object objectArg)throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        Map<TccLock,Object[]>  ret = new HashMap<>();

        String dbName = dogDb.dbName();

        String tableName = dogDb.tableName();

        String idHeader = "";

        Iterator iterator = ((Iterable)objectArg).iterator();

        List<Field> fieldList = new ArrayList<>();

        if(iterator.hasNext()){

            Object object = iterator.next();

            if (object.getClass().getAnnotation(DogTable.class) != null) {

                for (Field field : object.getClass().getDeclaredFields()) {

                    QueryArg queryarg = field.getAnnotation(QueryArg.class);

                    if (queryarg != null) {

                        field.setAccessible(true);

                        fieldList.add(field);

                        idHeader = idHeader + queryarg.argName();

                    }
                }
            }

        }


        iterator = ((Iterable)objectArg).iterator();

        while (iterator.hasNext()){

            Object ob = iterator.next();

            List<Object> retObjects = new ArrayList<>();

            String values = "";

            for(int j =0;j<fieldList.size();j++){

                values = values + fieldList.get(j).get(ob).toString();

                retObjects.add(fieldList.get(j).get(ob));
            }


            TccLock newLock = new TccLock(dbName+tableName+idHeader+values);

            ret.put(newLock,retObjects.toArray());
        }

        return  ret;
    };



    public Map<TccLock,Object[]> getLocksInIterator(Object objectArg)throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        Map<TccLock,Object[]>  ret = new HashMap<>();

        String dbName = dogDb.dbName();

        String tableName = dogDb.tableName();

        String idHeader = "";

        Iterator iterator = ((Iterable)objectArg).iterator();

        List<Field> fieldList = new ArrayList<>();

        if(iterator.hasNext()){

            Object object = iterator.next();

            if (object.getClass().getAnnotation(DogTable.class) != null) {

                for (Field field : object.getClass().getDeclaredFields()) {

                    QueryArg queryarg = field.getAnnotation(QueryArg.class);

                    if (queryarg != null) {

                        field.setAccessible(true);

                        fieldList.add(field);

                        idHeader = idHeader + queryarg.argName();

                    }
                }
            }

        }

        iterator = ((Iterable)objectArg).iterator();

        while (iterator.hasNext()){

            Object ob = iterator.next();

            String values = "";

            for (Field field : fieldList) {

                values = values + field.get(ob);

            }

            TccLock newLock = new TccLock(dbName+tableName+idHeader+values);

            ret.put(newLock,new Object[]{ob});
        }

        return  ret;
    };




    public Map<TccLock,Object[]> getLocksInDogTable(Object object)throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        Map<TccLock,Object[]>  ret = new HashMap<>();

        String dbName = dogDb.dbName();

        String tableName = dogDb.tableName();

        String idHeader = "";

        String values = "";

        List<Object> argObjs = new ArrayList<>();

        if (object.getClass().getAnnotation(DogTable.class) != null) {

            DogTable dogTableAnnotation = object.getClass().getAnnotation(DogTable.class);

            dbName = dogTableAnnotation.dbName();

            tableName = dogTableAnnotation.tableName();

            for (Field field : object.getClass().getDeclaredFields()) {

                QueryArg queryarg = field.getAnnotation(QueryArg.class);

                if (queryarg != null) {

                    field.setAccessible(true);

                    argObjs.add(field.get(object));

                    idHeader = idHeader + queryarg.argName();

                    values = values + field.get(object).toString();
                }
            }
        }

        TccLock tccLock = new TccLock(dbName + tableName + idHeader + values);

        ret.put(tccLock,argObjs.toArray());

        return  ret;
    }

    public Map<TccLock,Object[]> getLocksInParams()throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        Map<TccLock,Object[]>  ret = new HashMap<>();

        Annotation[][] annotations =  ReflectUtil.getParameterAnnotations(pjp);

        Object[] rawArgs = pjp.getArgs();

        if (rawArgs.length == 0) {

            return ret;
        }

        List<Object> objects = new ArrayList<>();

        String dbName = dogDb.dbName();

        String tableName = dogDb.tableName();

        String idHeader = "";

        String values = "";

        for (int i = 0; i < rawArgs.length; i++) {

            for (Annotation annotation : annotations[i]) {

                if (annotation instanceof QueryArg) {

                    objects.add(rawArgs[i]);

                    idHeader = idHeader + ((QueryArg)annotation).argName();

                    values = values + rawArgs[i].toString();

                }
            }

        }

        TccLock tccLock = new TccLock(dbName + tableName + idHeader + values);

        ret.put(tccLock,objects.toArray());

        return  ret;
    }

    public Map<TccLock, Object> getLocksDogTableOrListOfDogTable(Object object) throws IllegalAccessException {

        Map<TccLock, Object> result = new HashMap<>();

        if (object == null) {

            return result;

        }

        String dbName = "";

        String tableName = "";

        String idHeader = "";

        List<Field> queryfields = new ArrayList<>();

        if (Iterable.class.isAssignableFrom(object.getClass())) {

            Iterator iterator = ((Iterable) object).iterator();

            if (iterator.hasNext()) {

                Object obj = iterator.next();

                DogTable dogTable = obj.getClass().getAnnotation(DogTable.class);

                dbName = dogTable.dbName();

                tableName = dogTable.tableName();

                for (Field field : obj.getClass().getDeclaredFields()) {

                    QueryArg queryarg = field.getAnnotation(QueryArg.class);

                    if (queryarg != null) {

                        idHeader = idHeader + queryarg.argName();

                        field.setAccessible(true);

                        queryfields.add(field);
                    }
                }
            }

            iterator = ((Iterable) object).iterator();

            while (iterator.hasNext()) {

                String values = "";

                Object obj = iterator.next();

                for (Field field : queryfields) {

                    values = values + field.get(obj).toString();

                }

                TccLock tccLock = new TccLock(dbName + tableName + idHeader + values);

                result.put(tccLock, obj);
            }


        } else {

            DogTable dogTable = object.getClass().getAnnotation(DogTable.class);

            dbName = dogTable.dbName();

            tableName = dogTable.tableName();

            for (Field field : object.getClass().getDeclaredFields()) {

                QueryArg queryarg = field.getAnnotation(QueryArg.class);

                if (queryarg != null) {

                    idHeader = idHeader + queryarg.argName();

                    field.setAccessible(true);

                    queryfields.add(field);
                }
            }

            String values = "";


            for (Field field : queryfields) {

                values = values + field.get(object).toString();

            }

            TccLock tccLock = new TccLock(dbName + tableName + idHeader + values);

            result.put(tccLock, object);

        }

        return result;
    }


    /**
     * 一个参数只支持 DogTable 和 Iterator 两种，或者是单个被QueryArg标注的方法
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public List<Object> getQueryArgs() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        List<Object> result = new ArrayList<>();

        Annotation[][] annotations = ReflectUtil.getParameterAnnotations(pjp);

        Object[] args = pjp.getArgs();

        if (args.length == 0) {

            return result;

        } else if (args.length == 1) {

            Object firstObj = args[0];

            if (Iterable.class.isAssignableFrom(firstObj.getClass())) {

                result.add(firstObj);
            }

            if (firstObj.getClass().getAnnotation(DogTable.class) != null) {

                result.add(firstObj);

                return result;

            }

        }

        for (int i = 0; i < annotations.length; i++) {

            for (int j = 0; j < annotations[i].length; j++) {

                if (annotations[i][j] instanceof QueryArg) {

                    result.add(pjp.getArgs()[i]);

                }
            }

        }

        return result;
    }


    public List<Method> getCandidateMethods(String methodName) throws SecurityException {

        Class<?> clazz = dogDb.queryClass();

        return ReflectUtil.getMethods(clazz,methodName);
    }

}
