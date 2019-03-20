package org.dog.core.listener;

import org.dog.core.event.CallNodeOfflineEvent;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;

public interface CallNodeOfflineListener {

    void onCallEvent(CallNodeOfflineEvent var1) throws ConnectException, InterruptedException, NonexistException;

}
