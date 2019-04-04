package org.dog.database.core.aop;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.dog.core.common.ApplicationUtil;
import org.dog.core.entry.TccLock;
import org.dog.core.tccserver.ITccServer;
import org.dog.core.util.Pair;
import org.dog.core.util.ThreadManager;
import org.dog.database.core.ClazzInfo;
import org.dog.database.core.annotation.DogDb;
import org.dog.database.core.annotation.OperationType;
import org.dog.database.core.buffer.IDataBuffer;
import org.dog.database.core.util.ReflectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

@Component
@Aspect
public class DogDbAop {

    private static Logger logger = Logger.getLogger(DogDbAop.class);

    @Autowired
    ITccServer iTccServer;

    @Autowired
    IDataBuffer buffer;


    private void saveLockersInContext(DogDb db, Set<TccLock> newLocks) {

        Map<Object, Object> context = ThreadManager.getTccContext().getContext();

        ClazzInfo clazzInfo = ClazzInfo.createClazzInfo(db);

        if (context.containsKey(clazzInfo)) {

            Set<TccLock> values = (Set<TccLock>) context.get(clazzInfo);

            values.addAll(newLocks);

        } else {

            context.put(clazzInfo, newLocks);
        }

    }

    @Around("@annotation(org.dog.database.core.annotation.DogDb)  && @annotation(db)")
    public Object doAroundtransaction(ProceedingJoinPoint pjp, DogDb db) throws Throwable {

        Object result = null;

        DogAopHelper aopHelper = new DogAopHelper(pjp, db);

        Object repositoryObj = ApplicationUtil.getApplicationContext().getBean(db.repositoryClass());

        OperationType operationType = db.operationType();

        DataLoader factory = aopHelper.getDataLoader();

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

            if (ThreadManager.exsit() && operationType.equals(OperationType.UPDATEDATA)) {

                if (method != null) {

                    if(Iterable.class.isAssignableFrom(method.getReturnType())){

                        throw  new UnsupportedOperationException(method.getName());
                    }

                    while (iterator.hasNext()) {

                        Pair<TccLock, List<Object>> values = iterator.next();

                        Object queryData = method.invoke(repositoryObj, values.getValue().toArray());

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


            } else if (ThreadManager.exsit() && operationType.equals(OperationType.INSERTNEWDATA)) {

                if (method != null) {

                    while (iterator.hasNext()) {

                        Pair<TccLock, List<Object>> values = iterator.next();

                        lockedData.put(values.getKey(), values.getValue().toArray());

                    }

                }
            }

            if (lockedData.size() != 0) {

                Set<TccLock> newLocks = iTccServer.lock(lockedData.keySet());

                for (TccLock lock : newLocks) {

                    logger.info("Locks: " + lock.getKey());

                    buffer.buffData(lock, (lockedData.get(lock)));

                }

                saveLockersInContext(db, newLocks);
            }

            result = pjp.proceed();


        } catch (Exception e) {

            throw e;
        }

        return result;
    }

}

