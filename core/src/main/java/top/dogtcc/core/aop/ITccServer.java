package top.dogtcc.core.aop;

import top.dogtcc.core.entry.TccContext;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.jms.exception.ConnectException;
import top.dogtcc.core.jms.exception.NonexistException;
import org.aspectj.lang.ProceedingJoinPoint;
import top.dogtcc.core.common.IServer;

interface ITccServer extends IServer {

    Object tccTry(DogTcc tran, ProceedingJoinPoint point) throws Throwable;

    void tccCall(DogTcc transaction, DogCall call, TccContext context) throws ConnectException, NonexistException,InterruptedException;

}
