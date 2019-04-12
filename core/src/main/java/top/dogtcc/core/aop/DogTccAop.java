package top.dogtcc.core.aop;

import org.springframework.core.annotation.Order;
import top.dogtcc.core.annotation.DogTccAnnotation;
import top.dogtcc.core.ApplicationAutoConfig;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.common.ThreadManager;
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
@Order(-2)
public class DogTccAop {

    private static Logger logger = Logger.getLogger(DogTccAop.class);

    @Autowired
    ITccServer server;

    @Autowired
    private ApplicationAutoConfig applicationAutoConfig;

    @Around("@annotation(top.dogtcc.core.annotation.DogTccAnnotation)  && @annotation(ad) ")
    public Object doAroundtransaction(ProceedingJoinPoint pjp, DogTccAnnotation ad) throws Throwable {

        String tccName = (ad.Name().equals("") ? pjp.getSignature().toString().replace('.','_').replace(',','_').replace(' ','_').replace('(','_').replace(')','_'):ad.Name());

        if(ThreadManager.inTcc()){

            return  pjp.proceed();

        }else{

            try {

                DogTcc transaction = new DogTcc(applicationAutoConfig.getApplicationname(),tccName);

                logger.info("createTransaction:"+transaction.toString());

                ThreadManager.setTcc(transaction);

                Object object = server.tccTry(transaction,pjp);

                return  object;

            }catch (Exception e){

                throw  e;

            }finally {

                ThreadManager.clearTcc();
            }


        }
    }

}

