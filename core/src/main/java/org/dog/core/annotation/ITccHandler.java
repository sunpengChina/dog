package org.dog.core.annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.dog.core.entry.DogCall;
import org.dog.core.entry.DogTcc;

public interface ITccHandler {
     void preTryHandler(ProceedingJoinPoint pjp, DogTcc tcc, DogCall call,LockPool lockPool) throws Exception;
     void cancel(Object[] args, DogTcc tcc, DogCall call) throws Exception;
     void confirm(Object[] args,DogTcc tcc, DogCall call) throws  Exception;
     void exceptionHandler(ProceedingJoinPoint pjp, DogTcc tcc, DogCall call,Exception e) throws Exception;
}
