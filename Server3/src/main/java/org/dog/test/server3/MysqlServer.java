package org.dog.test.server3;

import org.dog.core.annotation.AbstractTryCompleteHandler;
import org.dog.core.annotation.DogCallAnnotation;
import org.springframework.stereotype.Component;

@Component
public class MysqlServer extends AbstractTryCompleteHandler {


    @DogCallAnnotation(Name = "insertMysql",RollbackClass = MysqlServer.class)
    public String insertMysql(String value){

        System.out.println("insertMysql");

        return  value;

    }

    public void cancel(Object[] args) {

        String value = (String) args[0];

        System.out.println("cancel tcc:"+value);
    }


    public void confirm(Object[] args) {

        String value = (String) args[0];

        System.out.println("confirm tcc:"+value);
    }



}
