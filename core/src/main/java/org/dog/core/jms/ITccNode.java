package org.dog.core.jms;

import org.dog.core.entry.DogTcc;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;

import org.dog.core.listener.CallNodeOfflineListener;
import org.dog.core.listener.TccAchievementListener;
import org.dog.core.listener.TccNodeOfflineListener;

public interface ITccNode {

    void registerTcc(DogTcc tcc) throws ConnectException, NonexistException,InterruptedException;

    void confirmTry(DogTcc tcc) throws ConnectException, InterruptedException;

    void cancelTry(DogTcc tcc) throws ConnectException ,InterruptedException;

    void clearTcc(DogTcc tcc) throws ConnectException, InterruptedException;

    void addTccAchievementListener(DogTcc transaction, TccAchievementListener listener) throws ConnectException, InterruptedException ;

    void addTccOfflineListner(TccNodeOfflineListener tccNodeOfflineListener) throws ConnectException,InterruptedException;

}
