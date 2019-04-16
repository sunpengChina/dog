package top.dogtcc.core.jms;

import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.TccContext;
import top.dogtcc.core.jms.exception.*;
import top.dogtcc.core.listener.CallNodeOfflineListener;
import top.dogtcc.core.listener.TccTryAchievementListener;


public interface ICallNode extends ILockPool,IContextManager{

    void registerCall(DogTcc tcc, DogCall call, TccContext context)  throws LockExsitException,TccNotExsitException, CallExsitException,ConnectException,InterruptedException;

    void confirmCall(DogTcc tcc, DogCall call,TccContext context) throws LockNotExsitException,TccNotExsitException,CallNotExsitException,ConnectException, InterruptedException;

    void addTryAchievementListener(DogTcc transaction, TccTryAchievementListener listener) throws TccNotExsitException,ConnectException,InterruptedException ;

    void addCallOfflineListener(CallNodeOfflineListener callNodeOfflineListener) ;


}
