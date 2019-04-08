package org.dog.core.listener;

import org.dog.core.event.TccAchievementEvent;
import org.dog.core.jms.exception.ConnectException;


public interface TccAchievementListener{

    void onTccEvent(TccAchievementEvent var1) throws ConnectException, InterruptedException;

}
