package top.dogtcc.core.listener;

import top.dogtcc.core.event.TccAchievementEvent;
import top.dogtcc.core.jms.exception.ConnectException;


public interface TccAchievementListener{

    void onTccEvent(TccAchievementEvent var1) throws ConnectException, InterruptedException;

}
