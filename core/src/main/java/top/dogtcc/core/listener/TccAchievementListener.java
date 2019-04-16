package top.dogtcc.core.listener;

import top.dogtcc.core.event.TccAchievementEvent;
import top.dogtcc.core.jms.exception.ConnectException;
import top.dogtcc.core.jms.exception.TccNotExsitException;


public interface TccAchievementListener{

    void onTccEvent(TccAchievementEvent var1) throws TccNotExsitException,ConnectException, InterruptedException;

}
