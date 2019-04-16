package top.dogtcc.core.aop;

import top.dogtcc.core.entry.TccContext;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.jms.exception.CallExsitException;
import top.dogtcc.core.jms.exception.ConnectException;
import org.aspectj.lang.ProceedingJoinPoint;
import top.dogtcc.core.common.IServer;
import top.dogtcc.core.jms.exception.LockExsitException;
import top.dogtcc.core.jms.exception.TccNotExsitException;

interface ITccServer extends IServer {

    Object tccTry(DogTcc tran, ProceedingJoinPoint point) throws Throwable;

    void tccCall(DogTcc transaction, DogCall call, TccContext context) throws LockExsitException,TccNotExsitException, CallExsitException,ConnectException,InterruptedException;

}
