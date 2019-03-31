package org.dog.core.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.dog.core.entry.DogCall;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.TccContext;
import org.dog.core.common.LockPool;

public interface ITccHandler {
     void preTryHandler(ProceedingJoinPoint pjp, DogTcc tcc, DogCall call, LockPool lockPool) throws Exception;
     void cancel(TccContext context,DogTcc tcc,  DogCall call) throws Exception;
     void confirm(TccContext context,DogTcc tcc, DogCall call) throws  Exception;
     void exceptionHandler(ProceedingJoinPoint pjp, DogTcc tcc, DogCall call,Exception e) throws Exception;
}
