package top.dogtcc.core.annotation;

import org.apache.log4j.Logger;
import top.dogtcc.core.common.ThreadManager;
import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.TccContext;
import top.dogtcc.core.entry.TccLock;
import top.dogtcc.core.jms.IContextManager;
import top.dogtcc.core.jms.ILockPool;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public abstract class TccHandler implements ITccHandler{


    private static Logger logger = Logger.getLogger(TccHandler.class);

    @Override
    public void before(  DogTcc tcc, DogCall call ) {


    }


    @Override
    public void exceptionHandler(DogTcc tcc, DogCall call, Exception e) throws Exception {


    }

    @Autowired
    private ILockPool lockPool;

    @Autowired
    private IContextManager iContextManager;


    final  public   void putDatas(Map<Object,Object> datas)throws Exception{

        if(ThreadManager.inTcc()){

            ThreadManager.getContext().getContext().putAll(datas);

            iContextManager.setContext(ThreadManager.currentTcc(),ThreadManager.currentCall(),ThreadManager.getContext());

        }

    }


    final public   Set<TccLock> lock (Set<TccLock> locks) throws Exception{

        if(ThreadManager.inTcc()) {

            return lockPool.lock(ThreadManager.currentTcc(), ThreadManager.currentCall(), locks);

        }

        return  null;
    }



    @Override
    public    void cancel(TccContext context, DogTcc tcc, DogCall call) {


        logger.info("cancel call: "+call);

    }

    @Override
    public    void confirm(TccContext context, DogTcc tcc, DogCall call) {

        logger.info("confirm call: "+call);

    }


}
