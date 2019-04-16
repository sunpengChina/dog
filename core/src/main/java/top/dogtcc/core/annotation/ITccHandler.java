package top.dogtcc.core.annotation;

import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.TccContext;
import top.dogtcc.core.entry.TccLock;


import java.util.Map;
import java.util.Set;


public interface ITccHandler {


     void before(DogTcc tcc, DogCall call) throws Exception;

     void cancel(TccContext context, DogTcc tcc, DogCall call) throws Exception;

     void confirm(TccContext context,DogTcc tcc, DogCall call) throws  Exception;

     void exceptionHandler( DogTcc tcc, DogCall call,Exception e) throws Exception;

}
