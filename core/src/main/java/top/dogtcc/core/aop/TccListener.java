package top.dogtcc.core.aop;

import top.dogtcc.core.annotation.ITccHandler;
import top.dogtcc.core.common.ContextBuffer;
import top.dogtcc.core.event.*;
import top.dogtcc.core.util.SpringContextUtil;
import top.dogtcc.core.entry.TccContext;
import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.DogTccStatus;
import top.dogtcc.core.jms.exception.ConnectException;
import top.dogtcc.core.jms.exception.NonexistException;
import top.dogtcc.core.listener.ITccListener;
import top.dogtcc.core.log.IErrorLog;
import top.dogtcc.core.jms.exception.StatusException;
import top.dogtcc.core.jms.IBroker;
import top.dogtcc.core.log.IHistoryLog;
import top.dogtcc.core.common.IBytePackConvert;
import org.apache.log4j.Logger;
import top.dogtcc.core.common.Pair;
import top.dogtcc.core.event.TccAchievementEvent;

import java.util.List;
import java.util.concurrent.Executor;

class TccListener implements ITccListener {

    private static Logger logger = Logger.getLogger(TccListener.class);

    private IBytePackConvert convert;

    private IBroker message;

    private ContextBuffer contextBuffer;

    private IErrorLog errorLog;

    private Executor executor;

    private IHistoryLog historylog;

    public TccListener(IBroker iMessage, IBytePackConvert convert, IErrorLog errorLog, ContextBuffer contextBuffer, IHistoryLog historylog, Executor executor) {

        this.message = iMessage;

        this.convert = convert;

        this.errorLog = errorLog;

        this.contextBuffer = contextBuffer;

        this.executor = executor;

        this.historylog = historylog;
    }


    @Override
    public void onTccEvent(TccAchievementEvent var1) throws ConnectException, InterruptedException {

        logger.info("TccAchievementEvent" + var1.getSource());

        message.clearTcc(var1.getSource());

        historylog.log(var1.getSource());

    }

    @Override
    public void onTccEvent(TccNodeOfflineEvent var1) throws ConnectException, InterruptedException {

        logger.info("TccNodeOfflineEvent" + var1.getSource());

        /**
         * try的过程中失败
         */
        if (var1.getSource().getStatus().equals(DogTccStatus.TRY)) {

            /**
             * 直接将tcc设置为失败
             */
            message.cancelTry(var1.getSource());

            message.addTccAchievementListener(var1.getSource(), this);

            /**
             * 清理过程中TCC掉线
             */
        } else {

            message.addTccAchievementListener(var1.getSource(), this);

        }

    }


    @Override
    public void onCallEvent(CallNodeOfflineEvent var1) throws ConnectException, InterruptedException, NonexistException {

        List<Pair<DogCall,byte[]>> offlineDogCalls =  var1.callPairs();

        for(Pair<DogCall,byte[]> e:offlineDogCalls){

            contextBuffer.put(var1.getTcc(),e.getKey(),(TccContext) convert.byteArrayToObject(e.getValue()));

        }

        logger.info("CallNodeOfflineEvent" + var1.getSource());

        message.addTryAchievementListener(var1.getTcc(), this);

    }


    /**
     *
     *
     * @param var1
     */
    @Override
    public void onTccEvent(TccTryAchievementEvent var1) {

        DogTcc transaction = var1.getSource();

        List<Pair<DogCall, TccContext>> calls = contextBuffer.find(transaction);

        contextBuffer.clear(transaction);

        try {

            if (transaction.getStatus().equals(DogTccStatus.CONFIRM) || transaction.getStatus().equals(DogTccStatus.CANCEL)) {

                for (Pair<DogCall, TccContext> e : calls) {

                    tryCompleteHandlerExecutor(transaction, e.getKey(), e.getValue());

                }

            } else {

                throw new StatusException();
            }

        } catch (Exception e) {

            logger.error(e);

        }
    }


    /**
     * 会调用：  message.confirmCall(tran,call) ;
     *
     * @param tran
     * @param call
     * @param context
     */
    protected void tryCompleteHandlerExecutor(DogTcc tran, DogCall call, TccContext context) {

        executor.execute(new Runnable() {

            @Override
            public void run() {

                try {

                    Class<?> rollbackClass  = Class.forName(context.getClassName());

                    ITccHandler rollback =  (ITccHandler) SpringContextUtil.getApplicationContext().getBean(rollbackClass);

                    if (tran.isSuccess()) {

                        rollback.confirm(context,tran,call);

                    } else {

                        rollback.cancel(context,tran,call);
                    }

                    message.confirmCall(tran, call,context);


                } catch (Exception f) {

                    logger.error(f);

                    if (tran.isSuccess()) {

                        errorLog.confirmError(tran, call, context);

                    } else {

                        errorLog.cancelError(tran, call, context);
                    }

                }

            }
        });


    }

}
