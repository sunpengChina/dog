package org.dog.core.jms;

import org.dog.core.common.Connectable;
import java.io.Closeable;


public interface IBroker extends Connectable, Closeable, SimultaneousBroker, AsynchronousBroker {


}
