package org.dog.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DogCallAnnotation {
    String Name() default "default";
    Class<? extends TryCompleteHandler> RollbackClass() ;
}
