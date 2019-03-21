package org.dog.core.tccserver;

import org.dog.core.ApplicationAutoConfig;
import org.dog.core.entry.BytePack;
import org.dog.core.entry.DogCall;
import org.dog.core.entry.DogTcc;
import org.dog.core.listener.ITccListener;
import org.dog.core.log.IErrorLog;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;
import org.dog.core.jms.IBroker;
import org.dog.core.log.IHistoryLog;
import org.dog.core.util.IBytePackConvert;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TccServer implements ITccServer{

    private static Logger logger = Logger.getLogger(TccServer.class);

    private ITccListener listener;

    protected TccBuffer runningTry;

    protected IBroker message;

    private ApplicationAutoConfig autoConfig;

    private IBytePackConvert convert;

    /**
     * 回调事务线程
     */
    ExecutorService rollbackExecutor;


    public TccServer (ApplicationAutoConfig autoConfig, IBroker iMessage, IBytePackConvert convert, IErrorLog errorLog, IHistoryLog historylog){

        this.runningTry = new TccBuffer();

        this.message = iMessage;

        this.autoConfig = autoConfig;

        this.convert = convert;

        this.rollbackExecutor = Executors.newCachedThreadPool();

        listener = new TccListener(autoConfig,iMessage,convert,errorLog,runningTry,historylog,rollbackExecutor);



    }

    @Override
    public Object tccTry(DogTcc tran, ProceedingJoinPoint point) throws Throwable {

        message.registerTcc(tran);

        try {

            logger.info("TCC远程调用:"+tran);

            Object result = point.proceed();

            logger.info("TCC返回:"+tran);

            message.confirmTry(tran);

            return  result;

        } catch (Throwable e) {

            logger.info("TCC报错:"+tran);

            logger.error(e);

            message.cancelTry(tran);

            throw  e;

        } finally {

            /**
             * 清空该事务
             */
            message.watchCallsConfirm(tran,listener);
        }

    }

    @Override
    public void tccCall(DogTcc transaction, DogCall call, BytePack dataPack) throws ConnectException, NonexistException, InterruptedException {

        message.registerCall(transaction,call,convert.objectToByteArray(dataPack));

        runningTry.addCall(transaction,call,dataPack);

        message.watchTccTryAchievement(transaction,listener);

        logger.info("watch:"  + transaction );

    }

    @Override
    public void connect() throws ConnectException, NonexistException, InterruptedException {

        this.message.connect();

        message.watchOffline(listener,listener);

        logger.info(autoConfig.getApplicationname()+": started");
    }

    @Override
    public void close() throws IOException {

        this.message.close();

        rollbackExecutor.shutdown();

    }


}
