package org.dog.core.annotation;

import org.dog.core.entry.DogCall;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.TccContext;


public interface ITccHandler {
     void before(DogTcc tcc, DogCall call) throws Exception;
     void cancel(TccContext context,DogTcc tcc,  DogCall call) throws Exception;
     void confirm(TccContext context,DogTcc tcc, DogCall call) throws  Exception;
     void exceptionHandler( DogTcc tcc, DogCall call,Exception e) throws Exception;
}
