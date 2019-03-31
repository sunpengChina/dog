package org.dog.core.tccserver;

import org.dog.core.common.Connectable;
import org.dog.core.common.LockPool;
import org.dog.core.entry.TccContext;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.DogCall;
import org.dog.core.entry.TccLock;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;
import org.aspectj.lang.ProceedingJoinPoint;

import java.io.Closeable;
import java.util.Set;

public interface ITccServer extends Connectable , Closeable , LockPool {

    Set<TccLock>  lock (Set<TccLock> locks) throws ConnectException,InterruptedException , NonexistException;

    Object tccTry(DogTcc tran, ProceedingJoinPoint point) throws Throwable;

    void tccCall(DogTcc transaction, DogCall call, TccContext context) throws  ConnectException, NonexistException,InterruptedException;

    void setCallContext(DogTcc transaction, DogCall call, TccContext context) throws  ConnectException, NonexistException,InterruptedException;

}
