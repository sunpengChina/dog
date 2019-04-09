package top.dogtcc.core.log;

import top.dogtcc.core.entry.TccContext;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.DogCall;


public interface IErrorLog {

    void  confirmError(DogTcc dogTran, DogCall call, TccContext context);

    void  cancelError(DogTcc dogTran, DogCall call, TccContext context);

}
