package org.dog.core.jms;

import org.dog.core.entry.DogCall;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.TccContext;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;
import org.dog.core.listener.CallNodeOfflineListener;
import org.dog.core.listener.TccTryAchievementListener;


public interface ICallNode extends ILockPool,IContextManager{

    void registerCall(DogTcc tcc, DogCall call, TccContext context)  throws ConnectException, NonexistException,InterruptedException;

    void confirmCall(DogTcc tcc, DogCall call,TccContext context) throws ConnectException, InterruptedException;

    void addTryAchievementListener(DogTcc transaction, TccTryAchievementListener listener) throws ConnectException,InterruptedException ;

    void addCallOfflineListener(CallNodeOfflineListener callNodeOfflineListener) throws ConnectException,InterruptedException ;


}
