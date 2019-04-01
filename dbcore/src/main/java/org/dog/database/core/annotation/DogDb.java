package org.dog.database.core.annotation;

import java.lang.annotation.*;
import java.lang.reflect.Method;


@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DogDb {
    Class<?> queryClass() ;
    String queryMethodName() ;
    Class<?>[] argClass() default {};
    String saveMethodName();
    OperationType type() default  OperationType.UPDATEDATA;
}
