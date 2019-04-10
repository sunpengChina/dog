package top.dogtcc.database.core.annotation;

import java.lang.annotation.*;


@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DogDb {

    String dbName() default  "";

    String tableName() default "";

    String queryMethodName() ;

    String deleteMethodName() default "";

    String saveMethodName();

    OperationType operationType() default  OperationType.UPDATEDATA;

}
