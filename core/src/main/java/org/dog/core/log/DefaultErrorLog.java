package org.dog.core.log;

import org.dog.core.entry.BytePack;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.DogCall;
import org.apache.log4j.Logger;

public class DefaultErrorLog implements IErrorLog {

    private static Logger logger = Logger.getLogger(DefaultErrorLog.class);

    @Override
    public void confirmError(DogTcc dogTran, DogCall call, BytePack pack) {

        logger.error("confirError"+dogTran+ "" + call);
    }

    @Override
    public void cancelError(DogTcc dogTran, DogCall call, BytePack pack) {


        logger.error("cancelError"+dogTran+ "" + call);
    }
}
