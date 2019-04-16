package top.dogtcc.core.jms;

import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.jms.exception.ConnectException;

import top.dogtcc.core.jms.exception.TccExsitException;
import top.dogtcc.core.jms.exception.TccNotExsitException;
import top.dogtcc.core.listener.TccAchievementListener;
import top.dogtcc.core.listener.TccNodeOfflineListener;

public interface ITccNode {

    void registerTcc(DogTcc tcc) throws TccExsitException,ConnectException,InterruptedException;

    void confirmTry(DogTcc tcc) throws TccNotExsitException,ConnectException, InterruptedException;

    void cancelTry(DogTcc tcc) throws TccNotExsitException,ConnectException ,InterruptedException;

    void clearTcc(DogTcc tcc) throws TccNotExsitException,ConnectException, InterruptedException;

    void addTccAchievementListener(DogTcc transaction, TccAchievementListener listener) throws TccNotExsitException,ConnectException, InterruptedException ;

    void addTccOfflineListner(TccNodeOfflineListener tccNodeOfflineListener);

}
