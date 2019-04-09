package top.dogtcc.core.jms;

import top.dogtcc.core.common.Connectable;

import java.io.Closeable;


public interface IBroker extends  Connectable, Closeable , ICallNode, ITccNode {

}
