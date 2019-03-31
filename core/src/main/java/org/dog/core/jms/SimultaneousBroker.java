package org.dog.core.jms;

import org.dog.core.common.LockPool;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.DogCall;
import org.dog.core.entry.TccContext;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;
import org.dog.core.jms.exception.NotStartTransactionException;

public interface SimultaneousBroker extends LockPool {

    void registerTcc(DogTcc tcc) throws ConnectException, NonexistException,InterruptedException;

    void confirmTry(DogTcc tcc) throws ConnectException, InterruptedException, NotStartTransactionException;

    void cancelTry(DogTcc tcc) throws NotStartTransactionException,ConnectException ,InterruptedException;

    void clearTcc(DogTcc tcc) throws ConnectException, InterruptedException;

    void registerCall(DogTcc tcc, DogCall call, TccContext context)  throws ConnectException, NonexistException,InterruptedException;

    void setCallContext(DogTcc tcc, DogCall call, TccContext context)  throws ConnectException, NonexistException,InterruptedException;

    void confirmCall(DogTcc tcc, DogCall call,TccContext context) throws ConnectException, InterruptedException;



}
