package org.dog.core.aop;

import org.dog.core.annotation.DogCallAnnotation;
import org.dog.core.entry.BytePack;
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
public class DogCallAop {


    private static Logger logger = Logger.getLogger(DogCallAop.class);

    @Autowired
    ITccServer server;


    @Around("@annotation(org.dog.core.annotation.DogCallAnnotation)  && @annotation(ad) ")
    public Object doAroundtransaction(ProceedingJoinPoint pjp, DogCallAnnotation ad) throws Throwable {

        Object result = null;

        try {


            DogTcc transaction = ThreadManager.getTransaction();

            /**
             * 说明在事务里面执行
             */
            if(transaction!=null){

                logger.info("本地事务调用："+transaction.toString()+": callname:"+ad.Name());

                DogCall localcaller = new DogCall(ad.Name());

                /**
                 * 将反射需要的类和参数封装起来
                 */
                BytePack pack = new BytePack(ad.RollbackClass().getName(),pjp.getArgs());

                /**
                 *
                 */
                server.tccCall(transaction,localcaller,pack);

            }else{

                logger.info("非本地事务调用 callname:"+ad.Name());
            }

            /**
             * 先注册再调用
             */
            result = pjp.proceed();

        } catch (Exception e) {

            logger.error("本地调用失败:"+e);

            throw  e;

        } finally {


        }

        return result;
    }
}
