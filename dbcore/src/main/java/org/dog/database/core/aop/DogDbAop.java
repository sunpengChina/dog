package org.dog.database.core.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.dog.core.common.ApplicationUtil;
import org.dog.core.entry.TccLock;
import org.dog.core.tccserver.ITccServer;
import org.dog.core.util.Pair;
import org.dog.core.util.ThreadManager;
import org.dog.database.core.SaveClazzInfo;
import org.dog.database.core.annotation.DogDb;
import org.dog.database.core.buffer.IDataBuffer;
import org.dog.database.core.util.ReflectUtil;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.dog.core.common.ApplicationUtil.getApplicationContext;

@Component
@Aspect
public class DogDbAop {

    @Autowired
    ITccServer iTccServer;

    @Autowired
    IDataBuffer buffer;

    @Around("@annotation(org.dog.database.core.annotation.DogDb)  && @annotation(db)")
    public Object doAroundtransaction(ProceedingJoinPoint pjp, DogDb db) throws Throwable {

        Object result = null;


        try {

            /**
             * 在事务中
             */
            if (ThreadManager.exsit()) {

                AopHelper aopHelper = new AopHelper(pjp, db);

                Pair<Method, Object[]> query = aopHelper.getQueryMethod();

                Object queryObj = ApplicationUtil.getApplicationContext().getBean(db.queryClass());

                Object queryData = query.getKey().invoke(queryObj, query.getValue());

                Map<TccLock, Object> locks = new HashMap<>();

                if (java.util.Optional.class.isAssignableFrom(queryData.getClass())) {

                    locks = aopHelper.getLocks(((java.util.Optional) queryData).get());

                } else {

                    locks = aopHelper.getLocks(queryData);
                }

                Set<TccLock> tobufferlocks = iTccServer.lock(locks.keySet());

                for (TccLock lock : tobufferlocks) {

                    buffer.buffData(lock, locks.get(lock));
                }

                Map<Object, Object> context = ThreadManager.getTccContext().getContext();

                SaveClazzInfo clazzInfo = new SaveClazzInfo(db.queryClass(),db.saveMethodName());

                if(context.containsKey(clazzInfo)){

                    Set<TccLock> values = (Set<TccLock>)context.get(clazzInfo);

                    values.addAll(tobufferlocks);


                }else{

                    context.put(clazzInfo,tobufferlocks);
                }

            }


            result = pjp.proceed();

        } catch (Exception e) {

            throw e;
        }

        return result;
    }

}

