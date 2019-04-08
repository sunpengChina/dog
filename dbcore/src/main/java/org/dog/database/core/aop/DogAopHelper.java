package org.dog.database.core.aop;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.dog.core.entry.TccLock;
import org.dog.core.common.Pair;
import org.dog.database.core.annotation.DogDb;
import org.dog.database.core.annotation.DogTable;
import org.dog.database.core.annotation.OperationType;
import org.dog.database.core.annotation.QueryArg;
import org.dog.database.core.util.ReflectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

class DogAopHelper {

    private static Logger logger = Logger.getLogger(DogAopHelper.class);

    public ParameterType getParameterType() {
        return parameterType;
    }

    private ParameterType parameterType;

    private int parameterLength;

    private OperationType operationType;

    private ProceedingJoinPoint pjp;

    private Annotation[][] annotations;

    private DogDb db;

    private int getMethodParameterLength(ParameterType parameterType, ProceedingJoinPoint joinPoint) throws  IllegalArgumentException, IllegalAccessException {

        if (parameterType.equals(ParameterType.NoParameter)) {

            return 0;

        }

        if (parameterType.equals(ParameterType.QueryArgs)) {

            int length = 0;

            for (Annotation[] parameterAnnotations : annotations) {

                for (Annotation annotation : parameterAnnotations) {

                    if (annotation instanceof QueryArg) {

                        length++;

                    }
                }

            }

            return length;
        }


        if (parameterType.equals(ParameterType.OneDogTable)) {

            return ReflectUtil.getFields(joinPoint.getArgs()[0], DogTable.class, QueryArg.class).size();
        }

        if (parameterType.equals(ParameterType.OneIterableOfDogTable)) {

            Object object = ((Iterable) joinPoint.getArgs()[0]).iterator().next();

            return ReflectUtil.getFields(object, DogTable.class, QueryArg.class).size();
        }

        return -1;
    }

    /**
     * 优先级 QueryArg > DogTable> Iterable DogTable
     * @param joinPoint
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private ParameterType getParameterType(ProceedingJoinPoint joinPoint) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        Object[] args = joinPoint.getArgs();

        if (args.length == 0) {

            return ParameterType.NoParameter;

        }

        for (Annotation[] parameterAnnotations : annotations) {

            for (Annotation annotation : parameterAnnotations) {

                if (annotation instanceof QueryArg) {

                    return ParameterType.QueryArgs;

                }
            }

        }

        if (args.length == 1) {

            Object firstParameter = args[0];

            if (firstParameter.getClass().getAnnotation(DogTable.class) != null) {

                return ParameterType.OneDogTable;

            }

            if (ReflectUtil.iterable(firstParameter)) {

                Iterator iterator = ((Iterable) firstParameter).iterator();

                if (iterator.hasNext()) {

                    Object obj = iterator.next();

                    if (obj.getClass().getAnnotation(DogTable.class) != null) {

                        return ParameterType.OneIterableOfDogTable;

                    }

                }

                return ParameterType.OneEmptyIterable;
            }

        }

        throw new IllegalArgumentException();
    }

    public Method getMethod() throws NoSuchMethodException, IllegalArgumentException {

        Class<?> clazz = db.repositoryClass();

        String methodName = "";

        if (db.operationType().equals(OperationType.INSERTNEWDATA)) {

            methodName = db.deleteMethodName();

        } else if (db.operationType().equals(OperationType.UPDATEDATA)) {

            methodName = db.queryMethodName();
        }

        if (!parameterType.equals(ParameterType.OneEmptyIterable)) {

            List<Method> methods = ReflectUtil.getMethods(clazz, methodName, parameterLength);

            if (methods.size() == 0) {

                throw new NoSuchMethodException(methodName);

            } else {

                return methods.get(0);
            }

        }

        return null;
    }

    public DogAopHelper(ProceedingJoinPoint pjp, DogDb db) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {


        annotations = ReflectUtil.getParameterAnnotations(pjp);

        parameterType = getParameterType(pjp);

        operationType = db.operationType();

        parameterLength = getMethodParameterLength(parameterType, pjp);


        this.db = db;

        this.pjp = pjp;

        logger.info("parameterType:" + parameterType + "   parameterLength:" + parameterLength + " operationType:" + operationType);

    }


    DataLoader getDataLoader() throws IllegalArgumentException {

        /**
         * (queryMethod == null)  无需查询 || (ParameterType.NoParameter) 无参数更新不锁 || (ParameterType.OneEmptyIterable) 更新的时候是空数组
         */
        if (parameterType.equals(ParameterType.NoParameter) || parameterType.equals(ParameterType.OneEmptyIterable)) {
            return new DataLoader() {
                @Override
                public Iterator<Pair<TccLock, List<Object>>> iterator() {
                    return new ArrayList<Pair<TccLock, List<Object>>>().iterator();
                }
            };
        }

        if (parameterType.equals(ParameterType.QueryArgs)) {

            return new DataLoader() {
                @Override
                public Iterator<Pair<TccLock, List<Object>>> iterator() {

                    ArrayList<Pair<TccLock, List<Object>>> arrayList = new ArrayList<>();

                    Pair<List<QueryArg>, List<Object>> queryArg = ReflectUtil.<QueryArg>getAnnotationedParameter(pjp.getArgs(), annotations, QueryArg.class);

                    TccLock tccLock = new DBLockBuilder().setDogDb(db).
                            setQueryArgs(queryArg.getKey()).build(queryArg.getValue());

                    arrayList.add(new Pair<>(tccLock, queryArg.getValue()));

                    return arrayList.iterator();

                }
            };

        }

        if (parameterType.equals(ParameterType.OneDogTable)) {

            return new DataLoader() {
                @Override
                public Iterator<Pair<TccLock, List<Object>>> iterator() {

                    ArrayList<Pair<TccLock, List<Object>>> arrayList = new ArrayList<>();

                    try {

                        DogTable dogTable = pjp.getArgs()[0].getClass().getAnnotation(DogTable.class);

                        Pair<List<QueryArg>, List<Field>> fields = ReflectUtil.<QueryArg>getAnnotationedFields(pjp.getArgs()[0].getClass(), QueryArg.class);

                        List<Object> objects = ReflectUtil.getFieldsValues(pjp.getArgs()[0], fields.getValue());

                        TccLock tccLock = new DBLockBuilder().setDogDb(db).setDogTable(dogTable).
                                setQueryArgs(fields.getKey()).build(objects);

                        arrayList.add(new Pair<>(tccLock, objects));

                    } catch (Exception e) {


                    }

                    return arrayList.iterator();

                }
            };

        }

        if (parameterType.equals(ParameterType.OneIterableOfDogTable)) {

            return new DataLoader() {
                @Override
                public Iterator<Pair<TccLock, List<Object>>> iterator() {

                    ArrayList<Pair<TccLock, List<Object>>> arrayList = new ArrayList<>();

                    try {

                        Object firstObj = ((Iterable) pjp.getArgs()[0]).iterator().next();

                        DogTable dogTable = firstObj.getClass().getAnnotation(DogTable.class);

                        Pair<List<QueryArg>, List<Field>> fields = ReflectUtil.<QueryArg>getAnnotationedFields(firstObj.getClass(), QueryArg.class);

                        Iterator iterator = ((Iterable) pjp.getArgs()[0]).iterator();

                        while (iterator.hasNext()) {

                            List<Object> objects = ReflectUtil.getFieldsValues(iterator.next(), fields.getValue());

                            TccLock tccLock = new DBLockBuilder().setDogDb(db).setDogTable(dogTable).
                                    setQueryArgs(fields.getKey()).build(objects);

                            arrayList.add(new Pair<>(tccLock, objects));

                        }

                    } catch (Exception e) {

                    }

                    return arrayList.iterator();
                }
            };

        }


        throw new IllegalArgumentException();
    }

}

