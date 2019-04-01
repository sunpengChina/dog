package org.dog.database.core.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.dog.core.entry.TccLock;
import org.dog.core.util.Pair;
import org.dog.database.core.annotation.DogDb;
import org.dog.database.core.annotation.DogTable;
import org.dog.database.core.annotation.QueryArg;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.aop.framework.ReflectiveMethodInvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

class AopHelper {


    private DogDb db;

    private ProceedingJoinPoint pjp;

    public AopHelper(ProceedingJoinPoint pjp, DogDb db) {

        this.db = db;

        this.pjp = pjp;

    }


    private List<Object> getDogTableQueryArgObjects(Object object) throws IllegalArgumentException, IllegalAccessException {

        List<Object> argObjs = new ArrayList<>();

        if (object.getClass().getAnnotation(DogTable.class) != null) {

            for (Field field : object.getClass().getDeclaredFields()) {
                QueryArg queryarg = field.getAnnotation(QueryArg.class);
                if (queryarg != null) {
                    field.setAccessible(true);
                    argObjs.add(field.get(object));
                }
            }
        }
        return argObjs;
    }


    private List<List<Object>> getIterableDogTableQueryArgObjects(Object object) throws IllegalArgumentException, IllegalAccessException {

        List<List<Object>> argObjs = new ArrayList<>();

        if (Iterable.class.isAssignableFrom(object.getClass())) {

            Iterator iterator = ((Iterable) object).iterator();

            if (iterator.hasNext()) {

                argObjs.add(getDogTableQueryArgObjects(iterator.next()));

            }
        }

        return argObjs;
    }


    private List<Object> getQueryArgInParamter(Object[] rawArgs) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        List<Object> argObjs = new ArrayList<>();

        Annotation[][] annotations = getMethodArgAnnotations(pjp);

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

    private  boolean allArgIsIterable(Class<?>[] clazzs){

        for(Class<?> clazz : clazzs){

            if(!Iterable.class.isAssignableFrom(clazz)){

                return  false;

            }
        }
        return  true;
    }

    private  boolean allArgNotIterable(Class<?>[] clazzs){

        for(Class<?> clazz : clazzs){

            if(Iterable.class.isAssignableFrom(clazz)){

                return  false;

            }
        }
        return  true;
    }


    /**
     * boolean 表示是否需要method迭代
     * @return
     * @throws NoSuchFieldException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public Pair<Boolean,Pair<Method, Object[]>> getQueryMethodNewVersion() throws NoSuchFieldException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException {

        List<Method> candidateQueryMethods = getCandidateQueryMethods();

        List<Object> rawArgs = getQueryArgs();

        /**
         * 无参情况
         */
        if (rawArgs.size() == 0) {

            Method method = getMethodWithArgSize(0, candidateQueryMethods);

            if (method != null) {

                return new Pair<>(false,new Pair<>(method, new Object[]{}));

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

                return new  Pair<>(false,new Pair<>(method, queryArgInParamter.toArray()));

            } else {

                throw new NoSuchMethodException();
            }

        }


        /**
         * 获取 DogTable 中的参数
         */
        List<Object> queryArgsInDogTable = getDogTableQueryArgObjects(rawArgs.get(0));

        if (queryArgsInDogTable.size() != 0) {

            Method method = getMethodWithArgSize(queryArgsInDogTable.size(), candidateQueryMethods);

            if (method != null) {

                return new  Pair<>(false,new Pair<>(method, queryArgsInDogTable.toArray()));

            } else {

                throw new NoSuchMethodException();
            }

        }

        /**
         * 直接通过 Iterator 中的参数传递
         */
        List<List<Object>> iterableObjects = getIterableDogTableQueryArgObjects(rawArgs.get(0));

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

                    if(allArgIsIterable(clazzs)){

                        List<List<Object>> ret = new ArrayList<>();

                        for(int i=0;i<argNum;i++){

                            ret.add(new ArrayList<Object>());
                        }

                        for(int j=0;j<iterableObjects.size();j++){

                            for(int i=0;i<argNum;i++){

                                ret.get(i).add(iterableObjects.get(j).get(i));

                            }
                        }

                        return new Pair<>(false,new  Pair<>(method, ret.toArray()));
                    }


                    if(allArgNotIterable(clazzs)){

                        return new Pair<>(true,new  Pair<>(method, iterableObjects.toArray()));
                    }
                }
            }

        }


        throw new NoSuchMethodException();
    }





    public Pair<Method, Object[]> getQueryMethod() throws NoSuchFieldException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException {

        List<Method> methods = getCandidateQueryMethods();

        List<Object> rawArgs = getQueryArgs();

        List<Object> dogTableArgs = new ArrayList<>();

        /**
         * 无参
         */
        if (rawArgs.size() == 0) {

            for (Method method : methods) {

                if (method.getParameterTypes().length == 0) {

                    return new Pair<>(method, new Object[]{});
                }
            }

            throw new NoSuchMethodException();
        }

        if (rawArgs.size() == 1) {

            /**
             * iterator 参数
             */
            if (Iterable.class.isAssignableFrom(rawArgs.get(0).getClass())) {

                for (Method method : methods) {

                    if (method.getParameterTypes().length == 1) {

                        if (Iterable.class.isAssignableFrom(method.getParameterTypes()[0].getClass())) {

                            return new Pair<>(method, new Object[]{rawArgs.get(0)});

                        }
                    }

                }

                throw new NoSuchMethodException();

            }

            /**
             * 参数为DogTable,把其中的参数取出来
             */
            if (rawArgs.get(0).getClass().getAnnotation(DogTable.class) != null) {

                for (Field field : rawArgs.get(0).getClass().getDeclaredFields()) {

                    QueryArg queryarg = field.getAnnotation(QueryArg.class);

                    if (queryarg != null) {

                        field.setAccessible(true);

                        dogTableArgs.add(field.get(rawArgs.get(0)));

                    }
                }

            }

        }

        for (Method method : methods) {

            if (method.getParameterTypes().length == rawArgs.size()) {

                if (rawArgs.get(0).getClass().getAnnotation(DogTable.class) == null && !Iterable.class.isAssignableFrom(method.getParameterTypes()[0].getClass())) {

                    return new Pair<>(method, rawArgs.toArray());

                }

            }

            if (method.getParameterTypes().length == dogTableArgs.size()) {

                return new Pair<>(method, dogTableArgs.toArray());
            }

        }

        throw new NoSuchMethodException();
    }


    public Map<TccLock, Object> getLocks(Object object) throws IllegalAccessException {

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
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException   //object  QueryArg  [多条]  ->   多条QueryArg
     *                                  //object  DogTable [1条]
     *                                  //object  iterator [1条]    ->   一条iterator
     */
    public List<Object> getQueryArgs() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        List<Object> result = new ArrayList<>();

        Annotation[][] annotations = getMethodArgAnnotations(pjp);

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


    public List<Method> getCandidateQueryMethods() throws SecurityException {

        List<Method> methods = new ArrayList<>();

        Class<?> clazz = db.queryClass();

        for (Method method : clazz.getMethods()) {

            if (method.getName().equals(db.queryMethodName())) {

                if (db.argClass().length == 0) {

                    methods.add(method);

                } else {

                    if (method.getParameterTypes().length == db.argClass().length) {

                        boolean hit = true;

                        for (int i = 0; i < db.argClass().length; i++) {

                            Class<?> methodarg = method.getParameterTypes()[i];

                            Class<?> aimType = db.argClass()[i];

                            if (!methodarg.equals(aimType)) {

                                hit = false;

                                break;
                            }

                        }

                        if (hit) {

                            methods.add(method);

                        }


                    } else {


                    }
                }
            }
        }

        return methods;
    }


    private static Annotation[][] getMethodArgAnnotations(ProceedingJoinPoint pjp) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        MethodInvocationProceedingJoinPoint methodJoinPoint = (MethodInvocationProceedingJoinPoint) pjp;

        Field methodInvocationfield = methodJoinPoint.getClass().getDeclaredField("methodInvocation");

        methodInvocationfield.setAccessible(true);

        //1参数的标注调用反射类
        Annotation[][] argAnnotations = ((ReflectiveMethodInvocation) methodInvocationfield.get(methodJoinPoint)).getMethod().getParameterAnnotations();


        return argAnnotations;
    }

}
