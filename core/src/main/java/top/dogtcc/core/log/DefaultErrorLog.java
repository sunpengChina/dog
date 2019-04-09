package top.dogtcc.core.log;

import top.dogtcc.core.entry.TccContext;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.DogCall;
import org.apache.log4j.Logger;

public class DefaultErrorLog implements IErrorLog {

    private static Logger logger = Logger.getLogger(DefaultErrorLog.class);

    @Override
    public void confirmError(DogTcc dogTran, DogCall call, TccContext pack) {

        logger.error("confirError"+dogTran+ "" + call);
    }

    @Override
    public void cancelError(DogTcc dogTran, DogCall call, TccContext pack) {


        logger.error("cancelError"+dogTran+ "" + call);
    }
}
