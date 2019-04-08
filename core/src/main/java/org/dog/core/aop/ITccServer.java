package org.dog.core.aop;

import org.dog.core.entry.TccContext;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.DogCall;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.dog.core.common.IServer;

interface ITccServer extends IServer {

    Object tccTry(DogTcc tran, ProceedingJoinPoint point) throws Throwable;

    void tccCall(DogTcc transaction, DogCall call, TccContext context) throws  ConnectException, NonexistException,InterruptedException;

}
