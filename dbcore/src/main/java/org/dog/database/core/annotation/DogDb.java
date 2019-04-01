package org.dog.database.core.annotation;

import java.lang.annotation.*;
import java.lang.reflect.Method;


@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DogDb {
    String dbName() default  "";
    String tableName() default "";
    Class<?> queryClass() ;
    String queryMethodName() ;
    String deleteMethodName();
    Class<?>[] argClass() default {};
    String saveMethodName();
    OperationType type() default  OperationType.UPDATEDATA;
}
