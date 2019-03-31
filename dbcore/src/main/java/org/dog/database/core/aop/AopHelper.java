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

    public Pair<Method, Object[]> getQueryMethod() throws NoSuchFieldException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException {

        List<Method> methods = getCandidateQueryMethods();

        List<Object> args = getQueryArgs();

        List<Object> dogTableArgs = new ArrayList<>();

        /**
         * 无参
         */
        if (args.size() == 0) {

            for (Method method : methods) {

                if (method.getParameterTypes().length == 0) {

                    return new Pair<>(method, null);
                }
            }

            throw new NoSuchMethodException();
        }

        if (args.size() == 1) {

            /**
             * iterator 参数
             */
            if (Iterable.class.isAssignableFrom(args.get(0).getClass())) {

                for (Method method : methods) {

                    if (method.getParameterTypes().length == 1) {

                        if (Iterable.class.isAssignableFrom(method.getParameterTypes()[0].getClass())) {

                            return new Pair<>(method, new Object[]{args.get(0)});

                        }
                    }

                }

                throw new NoSuchMethodException();

            }

            /**
             * 参数为DogTable
             */
            if (args.get(0).getClass().getAnnotation(DogTable.class) != null) {

                for (Field field : args.get(0).getClass().getDeclaredFields()) {

                    QueryArg queryarg = field.getAnnotation(QueryArg.class);

                    if (queryarg != null) {

                        field.setAccessible(true);

                        dogTableArgs.add(field.get(args.get(0)));

                    }
                }

            }

        }

        for (Method method : methods) {

            if (method.getParameterTypes().length == args.size()) {

                if (args.get(0).getClass().getAnnotation(DogTable.class) == null && !Iterable.class.isAssignableFrom(method.getParameterTypes()[0].getClass())) {

                    return new Pair<>(method, args.toArray());

                }

            }

            if (method.getParameterTypes().length == dogTableArgs.size()) {

                return new Pair<>(method, dogTableArgs.toArray());
            }

        }

        throw new NoSuchMethodException();
    }

    public Map<TccLock, Object> getLocks(Object object) throws IllegalAccessException {

        Map<TccLock, Object>  result = new HashMap<>();

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
