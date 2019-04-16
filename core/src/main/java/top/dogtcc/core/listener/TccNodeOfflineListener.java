package top.dogtcc.core.listener;

import top.dogtcc.core.event.TccNodeOfflineEvent;
import top.dogtcc.core.jms.exception.ConnectException;
import top.dogtcc.core.jms.exception.TccNotExsitException;


public interface TccNodeOfflineListener  {

    void onTccEvent(TccNodeOfflineEvent var1) throws TccNotExsitException,ConnectException, InterruptedException;
}
