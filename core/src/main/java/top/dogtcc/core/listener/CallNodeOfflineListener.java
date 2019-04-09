package top.dogtcc.core.listener;

import top.dogtcc.core.event.CallNodeOfflineEvent;
import top.dogtcc.core.jms.exception.ConnectException;
import top.dogtcc.core.jms.exception.NonexistException;

public interface CallNodeOfflineListener {

    void onCallEvent(CallNodeOfflineEvent var1) throws ConnectException, InterruptedException, NonexistException;

}
