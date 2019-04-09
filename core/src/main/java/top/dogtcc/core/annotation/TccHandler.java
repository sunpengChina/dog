package top.dogtcc.core.annotation;

import org.apache.log4j.Logger;
import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.TccContext;
import top.dogtcc.core.jms.IContextManager;
import top.dogtcc.core.jms.ILockPool;
import top.dogtcc.core.log.IErrorLog;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class TccHandler implements ITccHandler{

    private static Logger logger = Logger.getLogger(TccHandler.class);

    @Autowired
    ILockPool iLockPool;

    @Autowired
    IContextManager iContextManager;

    @Autowired
    protected IErrorLog errorLog;

    @Override
    public void exceptionHandler(DogTcc tcc, DogCall call, Exception e) throws Exception {


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
