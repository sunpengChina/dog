package org.dog.core.jms;

import org.dog.core.entry.DogTcc;
import org.dog.core.jms.exception.NotStartTransactionException;
import org.dog.core.listener.*;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.listener.CallNodeOfflineListener;
import org.dog.core.listener.TccAchievementListener;
import org.dog.core.listener.TccNodeOfflineListener;
import org.dog.core.listener.TccTryAchievementListener;

public interface AsynchronousBroker {

    void watchCallsConfirm(DogTcc transaction, TccAchievementListener listener) throws ConnectException, InterruptedException , NotStartTransactionException;

    void watchTccTryAchievement(DogTcc transaction, TccTryAchievementListener listener) throws ConnectException,InterruptedException ;

    void watchOffline(TccNodeOfflineListener tccNodeOfflineListener, CallNodeOfflineListener callNodeOfflineListener) throws ConnectException,InterruptedException ;


}
