package top.dogtcc.core.log;

import top.dogtcc.core.entry.DogTcc;
import org.apache.log4j.Logger;


public class DefaultHistoryLog implements IHistoryLog{

    private static Logger logger = Logger.getLogger(DefaultHistoryLog.class);

    @Override
    public void log(DogTcc transaction) {

        logger.info("完成TCC："+transaction);

    }
}
