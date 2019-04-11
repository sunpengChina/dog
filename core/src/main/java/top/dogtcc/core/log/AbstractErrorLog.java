package top.dogtcc.core.log;

import top.dogtcc.core.common.Pair;
import top.dogtcc.core.entry.TccContext;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.DogCall;
import org.apache.log4j.Logger;
import top.dogtcc.core.jmx.Error;
import top.dogtcc.core.jmx.ErrorLogMXBean;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractErrorLog implements ErrorLogMXBean, IErrorLog {

    List<Error> fails  = new ArrayList<>();

    @Override
    public List<Error> fails() {

        return fails;
    }

    private static Logger logger = Logger.getLogger(AbstractErrorLog.class);

    @Override
    public void confirmError(DogTcc dogTran, DogCall call, TccContext pack) {

        logger.error("confirError"+dogTran+ "" + call);
    }

    @Override
    public void cancelError(DogTcc dogTran, DogCall call, TccContext context) {

        if(fails.size() == 1024){

            fails.clear();

        }

        String errorString = "";

        for(Object arg :context.getArgs()){

            errorString = errorString +arg.getClass().getName()+"  "+arg.toString() + "  ";
        }

        fails.add(new Error(dogTran,call,context.getLockList(),errorString));

        logger.error("cancelError"+dogTran+ "" + call);
    }
}
