package top.dogtcc.core.jms;

import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.TccContext;
import top.dogtcc.core.jms.exception.CallNotExsitException;
import top.dogtcc.core.jms.exception.ConnectException;
import top.dogtcc.core.jms.exception.TccNotExsitException;

public interface IContextManager {

    void setContext(DogTcc tcc, DogCall call, TccContext context)  throws TccNotExsitException, CallNotExsitException,ConnectException,InterruptedException;

}
