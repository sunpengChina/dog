package top.dogtcc.core.jms;

import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.TccLock;
import top.dogtcc.core.jms.exception.CallNotExsitException;
import top.dogtcc.core.jms.exception.ConnectException;
import top.dogtcc.core.jms.exception.LockExsitException;
import top.dogtcc.core.jms.exception.TccNotExsitException;

import java.util.Set;

public interface ILockPool {

    Set<TccLock> lock (DogTcc tcc, DogCall call, Set<TccLock> locks) throws LockExsitException,TccNotExsitException, CallNotExsitException,ConnectException,InterruptedException ;

}
