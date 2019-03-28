package org.dog.core.log;

import org.dog.core.entry.TccContext;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.DogCall;


public interface IErrorLog {

    void  confirmError(DogTcc dogTran, DogCall call, TccContext pack);

    void  cancelError(DogTcc dogTran, DogCall call, TccContext pack);


}
