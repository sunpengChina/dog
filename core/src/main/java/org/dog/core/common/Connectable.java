package org.dog.core.common;

import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;

public interface Connectable {

    void connect()throws ConnectException,NonexistException,InterruptedException;
}
