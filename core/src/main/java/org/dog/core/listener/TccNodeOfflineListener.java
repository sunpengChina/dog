package org.dog.core.listener;

import org.dog.core.event.TccNodeOfflineEvent;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NotStartTransactionException;

public interface TccNodeOfflineListener  {

    void onTccEvent(TccNodeOfflineEvent var1) throws ConnectException, InterruptedException, NotStartTransactionException;
}
