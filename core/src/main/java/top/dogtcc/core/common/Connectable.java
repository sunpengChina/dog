package top.dogtcc.core.common;

import top.dogtcc.core.jms.exception.ConnectException;
import top.dogtcc.core.jms.exception.TccNotExsitException;

public interface Connectable {

    void connect()throws ConnectException,InterruptedException;

}
