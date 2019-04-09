package top.dogtcc.core.jms;

import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.TccContext;
import top.dogtcc.core.jms.exception.ConnectException;
import top.dogtcc.core.jms.exception.NonexistException;
import top.dogtcc.core.listener.CallNodeOfflineListener;
import top.dogtcc.core.listener.TccTryAchievementListener;


public interface ICallNode extends ILockPool,IContextManager{

    void registerCall(DogTcc tcc, DogCall call, TccContext context)  throws ConnectException, NonexistException,InterruptedException;

    void confirmCall(DogTcc tcc, DogCall call,TccContext context) throws ConnectException, InterruptedException;

    void addTryAchievementListener(DogTcc transaction, TccTryAchievementListener listener) throws ConnectException,InterruptedException ;

    void addCallOfflineListener(CallNodeOfflineListener callNodeOfflineListener) throws ConnectException,InterruptedException ;


}
