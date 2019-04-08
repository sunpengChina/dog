package org.dog.core.jms;

import org.dog.core.entry.DogCall;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.TccContext;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;

public interface IContextManager {

    void setContext(DogTcc tcc, DogCall call, TccContext context)  throws ConnectException, NonexistException,InterruptedException;

}
