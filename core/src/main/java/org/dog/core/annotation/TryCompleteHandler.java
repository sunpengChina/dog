package org.dog.core.annotation;

public interface TryCompleteHandler {
     void cancel(Object[] args) throws Exception;
     void confirm(Object[] args) throws  Exception;
}
