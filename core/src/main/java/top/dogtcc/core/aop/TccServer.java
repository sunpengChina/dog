package top.dogtcc.core.aop;

import top.dogtcc.core.ApplicationAutoConfig;
import top.dogtcc.core.entry.TccContext;
import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.listener.ITccListener;
import top.dogtcc.core.log.IErrorLog;
import top.dogtcc.core.jms.exception.ConnectException;
import top.dogtcc.core.jms.exception.NonexistException;
import top.dogtcc.core.jms.IBroker;
import top.dogtcc.core.log.IHistoryLog;
import top.dogtcc.core.common.ContextBuffer;
import top.dogtcc.core.common.IBytePackConvert;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
class TccServer implements ITccServer {

    private static Logger logger = Logger.getLogger(TccServer.class);

    private ApplicationAutoConfig autoConfig;

    private ITccListener listener;

    private ContextBuffer contextBuffer  = new ContextBuffer();;

    private IBroker message;

    ExecutorService rollbackExecutor;

    public TccServer(ApplicationAutoConfig autoConfig, IBroker iMessage, IBytePackConvert convert, IErrorLog errorLog, IHistoryLog historylog) {

        this.message = iMessage;

        this.autoConfig = autoConfig;

        this.rollbackExecutor = Executors.newCachedThreadPool();

        listener = new TccListener(iMessage, convert, errorLog, contextBuffer, historylog, rollbackExecutor);

    }



    @Override
    public Object tccTry(DogTcc tran, ProceedingJoinPoint point) throws Throwable {

        message.registerTcc(tran);

        try {

            logger.info("TCC远程调用:" + tran);

            Object result = point.proceed();

            logger.info("TCC返回:" + tran);

            message.confirmTry(tran);

            return result;

        } catch (Throwable e) {

            logger.info("TCC报错:" + tran);

            logger.error(e);

            message.cancelTry(tran);

            throw e;

        } finally {

            message.addTccAchievementListener(tran,listener);

        }

    }

    @Override
    public void tccCall(DogTcc transaction, DogCall call, TccContext context) throws ConnectException, NonexistException, InterruptedException {

        message.registerCall(transaction, call, context);

        contextBuffer.put(transaction, call, context);

        message.addTryAchievementListener(transaction, listener);

        logger.info("watch:" + transaction);

    }

    @Override
    public void connect() throws ConnectException, NonexistException, InterruptedException {

        this.message.connect();

        message.addCallOfflineListener(listener);

        message.addTccOfflineListner(listener);

        logger.info(autoConfig.getApplicationname() + ": started");
    }

    @Override
    public void close() throws IOException {

        this.message.close();

        rollbackExecutor.shutdown();

    }


}
