package top.dogtcc.core.annotation;

import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.TccContext;
import top.dogtcc.core.entry.TccLock;
import top.dogtcc.core.jms.exception.ConnectException;
import top.dogtcc.core.jms.exception.NonexistException;

import java.util.Map;
import java.util.Set;


public interface ITccHandler {

     Map<Object,Object> getDatas() throws Exception;
     void putDatas(Map<Object,Object> datas)throws Exception;
     Set<TccLock> getlocks (Set<TccLock> locks) throws Exception;
     Set<TccLock> lock (Set<TccLock> locks) throws Exception;


     void before(DogTcc tcc, DogCall call) throws Exception;
     void cancel(TccContext context, DogTcc tcc, DogCall call) throws Exception;
     void confirm(TccContext context,DogTcc tcc, DogCall call) throws  Exception;
     void exceptionHandler( DogTcc tcc, DogCall call,Exception e) throws Exception;

}
