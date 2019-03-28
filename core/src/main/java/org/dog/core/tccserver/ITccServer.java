package org.dog.core.tccserver;

import org.dog.core.annotation.LockPool;
import org.dog.core.common.Connectable;
import org.dog.core.entry.TccContext;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.DogCall;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;
import org.aspectj.lang.ProceedingJoinPoint;

import java.io.Closeable;

public interface ITccServer extends Connectable , Closeable , LockPool {

    Object tccTry(DogTcc tran, ProceedingJoinPoint point) throws Throwable;

    void tccCall(DogTcc transaction, DogCall call, TccContext dataPack) throws  ConnectException, NonexistException,InterruptedException;

    void setCallContext(DogTcc transaction, DogCall call, TccContext dataPack) throws  ConnectException, NonexistException,InterruptedException;


}
