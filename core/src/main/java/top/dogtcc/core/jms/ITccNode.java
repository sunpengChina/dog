package top.dogtcc.core.jms;

import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.jms.exception.ConnectException;
import top.dogtcc.core.jms.exception.NonexistException;

import top.dogtcc.core.listener.TccAchievementListener;
import top.dogtcc.core.listener.TccNodeOfflineListener;

public interface ITccNode {

    void registerTcc(DogTcc tcc) throws ConnectException, NonexistException,InterruptedException;

    void confirmTry(DogTcc tcc) throws ConnectException, InterruptedException;

    void cancelTry(DogTcc tcc) throws ConnectException ,InterruptedException;

    void clearTcc(DogTcc tcc) throws ConnectException, InterruptedException;

    void addTccAchievementListener(DogTcc transaction, TccAchievementListener listener) throws ConnectException, InterruptedException ;

    void addTccOfflineListner(TccNodeOfflineListener tccNodeOfflineListener) throws ConnectException,InterruptedException;

}
