package org.dog.core.aop;

import org.dog.core.annotation.DogTccAnnotation;
import org.dog.core.ApplicationAutoConfig;
import org.dog.core.entry.DogTcc;
import org.dog.core.common.ThreadManager;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Aspect
@PropertySource(value = {"classpath:application.properties"})
@ConfigurationProperties(prefix = "application")
public class DogTccAop {

    private static Logger logger = Logger.getLogger(DogTccAop.class);

    @Autowired
    ITccServer server;

    @Autowired
    private ApplicationAutoConfig applicationAutoConfig;

    @Around("@annotation(org.dog.core.annotation.DogTccAnnotation)  && @annotation(ad) ")
    public Object doAroundtransaction(ProceedingJoinPoint pjp, DogTccAnnotation ad) throws Throwable {

        String tccName = (ad.Name().equals("") ? pjp.getSignature().toString().replace('.','_').replace(',','_').replace(' ','_').replace('(','_').replace(')','_'):ad.Name());

        if(ThreadManager.inTcc()){

            return  pjp.proceed();


        }else{

            DogTcc transaction = new DogTcc(applicationAutoConfig.getApplicationname(),tccName);

            logger.info("createTransaction:"+transaction.toString());

            ThreadManager.setTcc(transaction);

            return  server.tccTry(transaction,pjp);

        }
    }

}

