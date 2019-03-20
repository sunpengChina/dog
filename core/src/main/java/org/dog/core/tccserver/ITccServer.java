package org.dog.core.tccserver;

import org.dog.core.common.Connectable;
import org.dog.core.entry.BytePack;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.DogCall;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;
import org.aspectj.lang.ProceedingJoinPoint;

import java.io.Closeable;

public interface ITccServer extends Connectable , Closeable  {

    Object tccTry(DogTcc tran, ProceedingJoinPoint point) throws Throwable;

    void tccCall(DogTcc transaction, DogCall call, BytePack dataPack) throws  ConnectException, NonexistException,InterruptedException;

}
