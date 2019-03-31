package org.dog.database.core.util;

import org.dog.core.entry.TccLock;
import org.dog.database.core.annotation.DogTable;
import org.dog.database.core.annotation.QueryArg;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class DatabaseUtil {

    public  static TccLock getLock(Object object) throws IllegalArgumentException, IllegalAccessException{

        Annotation[]  annotations = object.getClass().getAnnotations();

        String tableNameStr = null;

        String dbName = null;

        for(Annotation annotation:annotations){

            if(annotation instanceof DogTable){

                DogTable tableName = (DogTable)annotation;

                tableNameStr = tableName.tableName();

                dbName = tableName.dbName();

                break;

            }
        }

        if(tableNameStr!=null){


        }else {

            Field[] fields = object.getClass().getDeclaredFields();

            for(Field e: fields) {

                if (e.isAnnotationPresent(QueryArg.class)) {


                    e.setAccessible(true);

                    System.out.println(e.get(object));


                }
            }

        }

        return  null;
    }
}


