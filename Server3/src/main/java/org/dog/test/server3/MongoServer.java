package org.dog.test.server3;

import org.dog.core.annotation.TccHandler;
import org.dog.core.annotation.DogCallAnnotation;
import org.dog.test.server3.dao.ReturnOrder;
//import org.dog.test.server3.dao.ReturnOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoServer extends TccHandler {


//    @Autowired
//    private ReturnOrderRepository returnOrderRepository;

    @DogCallAnnotation(Name = "insertMongo", TccHandlerClass = MongoServer.class)
    public String insertMongo(String value){

//
//        returnOrderRepository.save(new ReturnOrder("110","awdaasdfs"));
//
//        List<ReturnOrder> resutl = returnOrderRepository.findAll();


        System.out.println("insertMongo");

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
