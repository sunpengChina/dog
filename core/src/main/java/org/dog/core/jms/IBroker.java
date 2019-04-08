package org.dog.core.jms;

import org.dog.core.common.Connectable;
import org.dog.core.common.IServer;

import java.io.Closeable;


public interface IBroker extends  Connectable, Closeable , ICallNode, ITccNode {

}
