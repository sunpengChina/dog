package top.dogtcc.core.jms;

import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.TccLock;
import top.dogtcc.core.jms.exception.ConnectException;
import top.dogtcc.core.jms.exception.NonexistException;

import java.util.Set;

public interface ILockPool {

    Set<TccLock> lock (DogTcc tcc, DogCall call, Set<TccLock> locks) throws ConnectException,InterruptedException , NonexistException;

}
