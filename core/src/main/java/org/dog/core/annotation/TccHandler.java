package org.dog.core.annotation;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.dog.core.entry.DogCall;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.TccContext;
import org.dog.core.entry.TccLock;

import java.util.HashSet;
import java.util.Set;

public  class TccHandler implements ITccHandler{


    @Override
    public void exceptionHandler(ProceedingJoinPoint pjp, DogTcc tcc, DogCall call,Exception e) throws Exception {


    }

    private static Logger logger = Logger.getLogger(TccHandler.class);

    @Override
    public void cancel(Object[] args, DogTcc tcc, DogCall call) {
        logger.info("cancel");
    }

    @Override
    public void confirm(Object[] args, DogTcc tcc, DogCall call) {
        logger.info("confirm");
    }

    @Override
    public void preTryHandler(ProceedingJoinPoint pjp, DogTcc tcc, DogCall call, LockPool lockPool, TccContext tccContext) {

        Set<TccLock> lockSet = new HashSet<>();

        lockSet.add(new TccLock("123"));

        try {

            lockPool.lock(tcc,call,lockSet,tccContext);

        }catch (Exception e){

            logger.error(e);
        }


        logger.info("prehandler");
    }

}
