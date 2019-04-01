package org.dog.database.core;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.dog.core.annotation.TccHandler;
import org.dog.core.common.ApplicationUtil;
import org.dog.core.common.LockPool;
import org.dog.core.entry.DogCall;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.TccContext;
import org.dog.core.entry.TccLock;
import org.dog.database.core.buffer.IDataBuffer;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class DbTccHandler extends TccHandler {

    private static Logger logger = Logger.getLogger(DbTccHandler.class);

    public DbTccHandler() {

        dataBuffer = ApplicationUtil.getApplicationContext().getBean(IDataBuffer.class);
    }

    private IDataBuffer dataBuffer;

    @Override
    public void exceptionHandler(ProceedingJoinPoint pjp, DogTcc tcc, DogCall call, Exception e) throws Exception {
        super.exceptionHandler(pjp, tcc, call, e);
    }


    private Method getMethod(Class<?> clazz,String methodName) throws NoSuchMethodException {

        for (Method method : clazz.getMethods()) {

            if (method.getName().equals(methodName) && method.getParameters().length == 1) {

                return method;

            }
        }
        throw new NoSuchMethodException(methodName);
    }


    @Override
    public void cancel(TccContext context, DogTcc tcc, DogCall call) {

        super.cancel(context, tcc, call);

        Map<Object, Object> locks = context.getContext();

        try {

            for (Map.Entry<Object, Object> values : locks.entrySet()) {

                ClazzInfo clazzInfo = (ClazzInfo) values.getKey();

                Object queryBean = ApplicationUtil.getApplicationContext().getBean(clazzInfo.getClazz());

                Set<TccLock> tccLocks = (Set<TccLock>) values.getValue();

                /**
                 *  修改型事务的回滚
                 */
                if (!clazzInfo.getSaveMethod().equals("")) {

                    Method method = getMethod(clazzInfo.getClazz(),clazzInfo.getSaveMethod());

                    /**
                     * 必然有缓存的锁
                     */
                    for (TccLock e : tccLocks) {

                        Object bufferedData = dataBuffer.getData(e);

                        method.invoke(queryBean, bufferedData);

                        dataBuffer.clearData(e);
                    }

                }

                /**
                 *  回滚插入型事务
                 */
                if (!clazzInfo.getDeleteMethod().equals("")) {

                    Method method = getMethod(clazzInfo.getClazz(),clazzInfo.getDeleteMethod());


                    for (TccLock e : tccLocks) {

                        Object bufferedData = dataBuffer.getData(e);

                        method.invoke(queryBean, bufferedData);

                        dataBuffer.clearData(e);
                    }


                }


            }


        } catch (Exception g) {

            logger.error(g);

            errorLog.cancelError(tcc, call, context);

        }


    }

    @Override
    public void confirm(TccContext context, DogTcc tcc, DogCall call) {

        super.confirm(context, tcc, call);

        for (TccLock lock : context.getLockList()) {

            /**
             * 有可能有锁无缓存
             */
            dataBuffer.clearData(lock);

        }

    }

    @Override
    public void preTryHandler(ProceedingJoinPoint pjp, DogTcc tcc, DogCall call, LockPool lockPool) {
        super.preTryHandler(pjp, tcc, call, lockPool);
    }
}
