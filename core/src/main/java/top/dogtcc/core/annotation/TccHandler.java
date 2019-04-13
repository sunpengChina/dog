package top.dogtcc.core.annotation;

import org.apache.log4j.Logger;
import top.dogtcc.core.common.ThreadManager;
import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.TccContext;
import top.dogtcc.core.entry.TccLock;
import top.dogtcc.core.jms.IContextManager;
import top.dogtcc.core.jms.ILockPool;
import top.dogtcc.core.log.IErrorLog;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

public abstract class TccHandler implements ITccHandler{

    private static Logger logger = Logger.getLogger(TccHandler.class);

    @Autowired
    private ILockPool lockPool;

    @Autowired
    private IContextManager iContextManager;

    public  Set<TccLock> getlocks (Set<TccLock> locks) throws Exception{

        return  ThreadManager.getContext().getLockList();

    }

    public  Map<Object,Object> getDatas() throws Exception{

        return ThreadManager.getContext().getContext();

    }

    public void putDatas(Map<Object,Object> datas)throws Exception{

        ThreadManager.getContext().getContext().putAll(datas);

        iContextManager.setContext(ThreadManager.currentTcc(),ThreadManager.currentCall(),ThreadManager.getContext());
    }


    public Set<TccLock> lock (Set<TccLock> locks) throws Exception{

        return  lockPool.lock(ThreadManager.currentTcc(),ThreadManager.currentCall(),locks);
    }




    @Override
    public void exceptionHandler(DogTcc tcc, DogCall call, Exception e) throws Exception {


    }

    @Override
    public void cancel(TccContext context, DogTcc tcc, DogCall call) {
        logger.info("cancel"+ tcc.toString());
    }

    @Override
    public void confirm(TccContext context, DogTcc tcc, DogCall call) {
        logger.info("confirm"+ tcc.toString());
    }

    @Override
    public void before(  DogTcc tcc, DogCall call ) {

        logger.info("before");
    }

}
