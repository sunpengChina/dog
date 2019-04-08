package org.dog.core.jms;

import org.dog.core.entry.DogCall;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.TccLock;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;

import java.util.Set;

public interface ILockPool {

    Set<TccLock> lock (DogTcc tcc, DogCall call, Set<TccLock> locks) throws ConnectException,InterruptedException , NonexistException;

}
