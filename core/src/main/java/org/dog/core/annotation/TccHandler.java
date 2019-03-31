package org.dog.core.annotation;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.dog.core.entry.DogCall;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.TccContext;
import org.dog.core.entry.TccLock;
import org.dog.core.common.LockPool;
import org.dog.core.log.IErrorLog;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

public abstract class TccHandler implements ITccHandler{

    @Autowired
    protected  IErrorLog errorLog;

    @Override
    public void exceptionHandler(ProceedingJoinPoint pjp, DogTcc tcc, DogCall call,Exception e) throws Exception {


    }

    private static Logger logger = Logger.getLogger(TccHandler.class);

    @Override
    public void cancel(TccContext context, DogTcc tcc, DogCall call) {
        logger.info("cancel");
    }

    @Override
    public void confirm(TccContext context, DogTcc tcc, DogCall call) {
        logger.info("confirm");
    }

    @Override
    public void preTryHandler(ProceedingJoinPoint pjp, DogTcc tcc, DogCall call, LockPool lockPool) {

        logger.info("prehandler");
    }

}
