package top.dogtcc.core.log;

import top.dogtcc.core.entry.DogTcc;
import org.apache.log4j.Logger;


public abstract  class AbstractHistoryLog implements IHistoryLog{

    private static Logger logger = Logger.getLogger(AbstractHistoryLog.class);

    @Override
    public void log(DogTcc transaction) {

        logger.info("完成TCC："+transaction);

    }
}
