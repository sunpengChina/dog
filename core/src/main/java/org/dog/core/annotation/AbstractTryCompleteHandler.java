package org.dog.core.annotation;

import org.apache.log4j.Logger;

public  class AbstractTryCompleteHandler implements TryCompleteHandler {

    private static Logger logger = Logger.getLogger(AbstractTryCompleteHandler.class);

    @Override
    public void cancel(Object[] args) {
        logger.info("cancel");
    }

    @Override
    public void confirm(Object[] args) {
        logger.info("confirm");
    }


}
