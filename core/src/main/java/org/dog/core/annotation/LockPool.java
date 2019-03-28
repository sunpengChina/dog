package org.dog.core.annotation;

import org.dog.core.entry.DogCall;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.TccContext;
import org.dog.core.entry.TccLock;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;

import java.util.List;
import java.util.Set;

public interface LockPool {

    void lock (DogTcc transaction, DogCall call, Set<TccLock>  locks,TccContext tccContext) throws ConnectException,InterruptedException , NonexistException;

}
