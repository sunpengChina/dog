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
import org.dog.core.tccserver.TccServer;
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


    private Method getMethod(SaveClazzInfo clazzInfo) throws NoSuchMethodException {

        for (Method method : clazzInfo.getClazz().getMethods()) {

            if (method.getName().equals(clazzInfo.getSaveMethod()) && method.getParameters().length == 1) {

                return method;

            }
        }

        throw new NoSuchMethodException(clazzInfo.getSaveMethod());
    }


    @Override
    public void cancel(TccContext context, DogTcc tcc, DogCall call) {

        super.cancel(context, tcc, call);

        Map<Object, Object> locks = context.getContext();

        try {

            for (Map.Entry<Object, Object> values : locks.entrySet()) {

                SaveClazzInfo saveClazzInfo = (SaveClazzInfo) values.getKey();

                Object queryBean = ApplicationUtil.getApplicationContext().getBean(saveClazzInfo.getClazz());

                Set<TccLock> tccLocks = (Set<TccLock>) values.getValue();

                Method method = getMethod(saveClazzInfo);

                for (TccLock e : tccLocks) {

                    Object bufferedData = dataBuffer.getData(e);

                    method.invoke(queryBean,bufferedData);

                    dataBuffer.clearData(e);
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

        for(TccLock lock : context.getLockList()){

            dataBuffer.clearData(lock);

        }

    }

    @Override
    public void preTryHandler(ProceedingJoinPoint pjp, DogTcc tcc, DogCall call, LockPool lockPool) {
        super.preTryHandler(pjp, tcc, call, lockPool);
    }
}
