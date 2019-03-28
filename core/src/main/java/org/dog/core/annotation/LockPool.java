package org.dog.core.annotation;

import org.dog.core.entry.DogCall;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.TccContext;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;

public interface LockPool {

    boolean lock (DogTcc transaction, DogCall call, TccContext dataPack) throws ConnectException,InterruptedException , NonexistException;

}
