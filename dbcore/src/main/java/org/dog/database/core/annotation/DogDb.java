package org.dog.database.core.annotation;

import java.lang.annotation.*;
import java.lang.reflect.Method;


@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DogDb {

    String dbName() default  "";

    String tableName() default "";

    Class<?> repositoryClass() ;

    String queryMethodName() ;

    String deleteMethodName() default "";

    String saveMethodName();

    OperationType operationType() default  OperationType.UPDATEDATA;

}
