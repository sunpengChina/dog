package org.dog.core.jms;

import org.dog.core.entry.DogTcc;
import org.dog.core.entry.DogCall;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;
import org.dog.core.jms.exception.NotStartTransactionException;

public interface SimultaneousBroker {

    void registerTcc(DogTcc tcc) throws ConnectException, NonexistException,InterruptedException;

    void confirmTry(DogTcc tcc) throws ConnectException, InterruptedException, NotStartTransactionException;

    void cancelTry(DogTcc tcc) throws NotStartTransactionException,ConnectException ,InterruptedException;

    void clearTcc(DogTcc tcc) throws ConnectException, InterruptedException;

    void registerCall(DogTcc tcc, DogCall call, byte[] data)  throws ConnectException, NonexistException,InterruptedException;

    void confirmCall(DogTcc tcc, DogCall call) throws ConnectException, InterruptedException;

}
