package com.server2;

import org.dog.core.annotation.AbstractTryCompleteHandler;
import org.dog.core.annotation.DogCallAnnotation;
import org.springframework.stereotype.Component;

@Component
public class AbstractTryListener extends AbstractTryCompleteHandler {

    @DogCallAnnotation(Name = "updatemysql",RollbackClass = AbstractTryListener.class)
    public String insertToMysql(String value){

        System.out.println("updatemysql");

        return  value;

    }

    @DogCallAnnotation(Name = "insertMongo",RollbackClass = AbstractTryListener.class)
    public String insertMongo(String value){

        System.out.println("insertMongo");

        return  value;

    }


}
