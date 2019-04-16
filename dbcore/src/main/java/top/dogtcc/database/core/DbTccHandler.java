package top.dogtcc.database.core;

import org.apache.log4j.Logger;
import top.dogtcc.core.annotation.TccHandler;
import top.dogtcc.core.util.SpringContextUtil;

import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.TccContext;
import top.dogtcc.core.entry.TccLock;
import top.dogtcc.database.core.annotation.OperationType;
import top.dogtcc.database.core.buffer.IDataBuffer;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class DbTccHandler extends TccHandler {

    private static Logger logger = Logger.getLogger(DbTccHandler.class);

    public DbTccHandler() {

        dataBuffer = SpringContextUtil.getApplicationContext().getBean(IDataBuffer.class);
    }

    private IDataBuffer dataBuffer;

    @Override
    public void exceptionHandler(  DogTcc tcc, DogCall call, Exception e) throws Exception {
        super.exceptionHandler(  tcc, call, e);
    }


    @Override
    public void cancel(TccContext context, DogTcc tcc, DogCall call) {

        super.cancel(context, tcc, call);

        Map<Object, Object> locks = context.getContext();

        try {

            for (Map.Entry<Object, Object> values : locks.entrySet()) {

                ClazzInfo clazzInfo = (ClazzInfo) values.getKey();

                Object repositoryBean = SpringContextUtil.getApplicationContext().getBean(clazzInfo.getClazz());

                Set<TccLock> tccLocks = (Set<TccLock>) values.getValue();

                Method method = clazzInfo.method(repositoryBean);

                /**
                 * 必然有缓存的锁
                 */
                for (TccLock e : tccLocks) {

                    Object bufferedData = dataBuffer.getData(e);

                    method.setAccessible(true);

                    if(clazzInfo.getOperationType().equals(OperationType.INSERTNEWDATA)){

                        method.invoke(repositoryBean, (Object[])bufferedData);

                    }else {

                        method.invoke(repositoryBean, bufferedData);
                    }


                    logger.info("Cancel method:"+method.getName());

                    logger.info("Cancel parameter:"+bufferedData);

                    dataBuffer.clearData(e);
                }

            }

        } catch (Exception g) {

            logger.error(g);

        }


    }

    @Override
    public void confirm(TccContext context, DogTcc tcc, DogCall call) {

        super.confirm(context, tcc, call);

        for (TccLock lock : context.getLockList()) {

            dataBuffer.clearData(lock);

        }

    }

    @Override
    public void before( DogTcc tcc, DogCall call ) {
        super.before(  tcc, call );
    }


}
