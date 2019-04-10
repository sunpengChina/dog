package top.dogtcc.database.core.aop;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import top.dogtcc.core.jms.ILockPool;
import top.dogtcc.core.entry.TccLock;
import top.dogtcc.core.common.Pair;
import top.dogtcc.core.common.ThreadManager;
import top.dogtcc.database.core.ClazzInfo;
import top.dogtcc.database.core.annotation.DogDb;
import top.dogtcc.database.core.annotation.OperationType;
import top.dogtcc.database.core.buffer.IDataBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.dogtcc.database.core.util.ReflectUtil;

import java.lang.reflect.Method;
import java.util.*;

@Component
@Aspect
public class DogDbAop {

    private static Logger logger = Logger.getLogger(DogDbAop.class);

    @Autowired
    ILockPool iLockPool;

    @Autowired
    IDataBuffer buffer;

    private void saveLockersInContext(DogDb db, Set<TccLock> newLocks,Class<?> clazz) {

        Map<Object, Object> context = ThreadManager.getContext().getContext();

        ClazzInfo clazzInfo = ClazzInfo.createClazzInfo(db,clazz);

        if (context.containsKey(clazzInfo)) {

            Set<TccLock> values = (Set<TccLock>) context.get(clazzInfo);

            values.addAll(newLocks);

        } else {

            context.put(clazzInfo, newLocks);
        }

    }

    @Around("@annotation(top.dogtcc.database.core.annotation.DogDb)  && @annotation(db)")
    public Object doAroundtransaction(ProceedingJoinPoint pjp, DogDb db) throws Throwable {

        Object result = null;



        DogAopHelper aopHelper = new DogAopHelper(pjp, db, ReflectUtil.getTargetClass(pjp));

                //ApplicationUtil.getApplicationContext().getBean(db.repositoryClass());

        OperationType operationType = db.operationType();

        DataContainer factory = aopHelper.getDataLoader();

        Iterator<Pair<TccLock, List<Object>>> iterator = factory.iterator();

        Method method = aopHelper.getMethod();

        if(method!=null) {

            logger.info("repository methodName: " + method.getName());

        }

        /**
         *key:锁  value:被锁的行数据;  目前不支持method 方法的返回值为 Iterable;
         */
        Map<TccLock, Object> lockedData = new HashMap<>();

        try {

            if (ThreadManager.inTcc() && operationType.equals(OperationType.UPDATEDATA)) {

                if (method != null) {

                    if(Iterable.class.isAssignableFrom(method.getReturnType())){

                        throw  new UnsupportedOperationException(method.getName());
                    }

                    while (iterator.hasNext()) {

                        Pair<TccLock, List<Object>> values = iterator.next();

                        Object queryData = method.invoke(ReflectUtil.getTarget(pjp), values.getValue().toArray());

                        if (queryData!=null) {

                            if(java.util.Optional.class.isAssignableFrom(queryData.getClass())){

                                if(((Optional)queryData).isPresent()){

                                    lockedData.put(values.getKey(), ((Optional)queryData).get());

                                }

                            }else {

                                lockedData.put(values.getKey(), queryData);
                            }

                        }

                    }

                }


            } else if (ThreadManager.inTcc() && operationType.equals(OperationType.INSERTNEWDATA)) {

                if (method != null) {

                    while (iterator.hasNext()) {

                        Pair<TccLock, List<Object>> values = iterator.next();

                        lockedData.put(values.getKey(), values.getValue().toArray());

                    }

                }
            }

            if (lockedData.size() != 0) {

                Set<TccLock>   newLocks = iLockPool.lock(ThreadManager.currentTcc(),ThreadManager.currentCall(),lockedData.keySet());

                for (TccLock lock : newLocks) {

                    logger.info("Locks: " + lock.getKey());

                    buffer.buffData(lock, (lockedData.get(lock)));

                }

                saveLockersInContext(db, newLocks,ReflectUtil.getTargetClass(pjp));
            }

            result = pjp.proceed();


        } catch (Exception e) {

            throw e;
        }

        return result;
    }

}

