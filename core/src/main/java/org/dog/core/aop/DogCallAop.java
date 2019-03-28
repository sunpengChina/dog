package org.dog.core.aop;

import org.dog.core.ApplicationAutoConfig;
import org.dog.core.annotation.DogCallAnnotation;
import org.dog.core.annotation.ITccHandler;
import org.dog.core.annotation.LockPool;
import org.dog.core.common.ApplicationUtil;
import org.dog.core.entry.TccContext;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.DogCall;
import org.dog.core.tccserver.ITccServer;
import org.dog.core.util.ThreadManager;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Aspect
public class DogCallAop implements LockPool {


    @Autowired
    ApplicationAutoConfig config;


    private static Logger logger = Logger.getLogger(DogCallAop.class);

    @Autowired
    ITccServer server;


    @Around("@annotation(org.dog.core.annotation.DogCallAnnotation)  && @annotation(ad) ")
    public Object doAroundtransaction(ProceedingJoinPoint pjp, DogCallAnnotation ad) throws Throwable {

        String callName = (ad.Name().equals("")? pjp.getSignature().toString().replace('.','_').replace(',','_').replace(' ','_').replace('(','_').replace(')','_'):ad.Name());

        Object result = null;

        ITccHandler tccHandler = null;

        DogTcc transaction = null;

        DogCall localcaller = null;

        try {


            transaction = ThreadManager.getTransaction();

            /**
             * 说明在事务里面执行
             */
            if(transaction!=null){

                logger.info("本地事务调用："+transaction.toString()+": callname:"+callName);

                localcaller = new DogCall(callName,config.getApplicationname());

                /**
                 * 事务上下文
                 */
                TccContext tccContext = new TccContext(ad.TccHandlerClass().getName(),pjp.getArgs());

                /**
                 * 注册call
                 */
                server.tccCall(transaction,localcaller,tccContext);

                // 注册前的操作
                Class<?> tccHandlerClass  = Class.forName(tccContext.getClassName());

                tccHandler =  (ITccHandler) ApplicationUtil.getApplicationContext().getBean(tccHandlerClass);

                tccHandler.preTryHandler(pjp,transaction,localcaller,this);

                server.setCallContext(transaction,localcaller,tccContext);

            }else{

                logger.info("非本地事务调用 callname:"+callName);
            }

            /**
             * 先注册再调用
             */
            result = pjp.proceed();

        } catch (Exception e) {

            if(tccHandler !=null) {

                tccHandler.exceptionHandler(pjp, transaction, localcaller,e);
            }

            logger.error("本地调用失败:"+e);

            throw  e;

        } finally {


        }

        return result;
    }

    @Override
    public boolean lock(DogTcc transaction, DogCall call, TccContext dataPack) {




        return false;
    }
}
