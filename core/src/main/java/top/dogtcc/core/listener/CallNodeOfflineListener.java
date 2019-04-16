package top.dogtcc.core.listener;

import top.dogtcc.core.event.CallNodeOfflineEvent;
import top.dogtcc.core.jms.exception.ConnectException;
import top.dogtcc.core.jms.exception.TccNotExsitException;

public interface CallNodeOfflineListener {

    void onCallEvent(CallNodeOfflineEvent var1) throws ConnectException, InterruptedException, IllegalStateException;

}
