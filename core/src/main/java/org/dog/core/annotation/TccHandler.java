package org.dog.core.annotation;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.dog.core.entry.DogCall;
import org.dog.core.entry.DogTcc;

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
    public void preTryHandler(ProceedingJoinPoint pjp, DogTcc tcc, DogCall call,LockPool lockPool) {
        logger.info("prehandler");
    }

}
