package org.dog.core.annotation;

import org.apache.log4j.Logger;
import org.dog.core.entry.DogCall;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.TccContext;
import org.dog.core.jms.IContextManager;
import org.dog.core.jms.ILockPool;
import org.dog.core.log.IErrorLog;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class TccHandler implements ITccHandler{

    private static Logger logger = Logger.getLogger(TccHandler.class);

    @Autowired
    ILockPool iLockPool;

    @Autowired
    IContextManager iContextManager;

    @Autowired
    protected  IErrorLog errorLog;

    @Override
    public void exceptionHandler( DogTcc tcc, DogCall call,Exception e) throws Exception {


    }

    @Override
    public void cancel(TccContext context, DogTcc tcc, DogCall call) {
        logger.info("cancel");
    }

    @Override
    public void confirm(TccContext context, DogTcc tcc, DogCall call) {
        logger.info("confirm");
    }

    @Override
    public void before(  DogTcc tcc, DogCall call ) {

        logger.info("before");
    }

}
