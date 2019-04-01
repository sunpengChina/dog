package org.dog.test.server3;

import org.dog.core.annotation.TccHandler;
import org.dog.core.annotation.DogCallAnnotation;
import org.dog.database.core.DbTccHandler;
import org.dog.test.server3.dao.ReturnOrder;
import org.dog.test.server3.dao.ReturnOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Component
public class MongoServer extends DbTccHandler {


    @Autowired
    private ReturnOrderRepository returnOrderRepository;


    @DogCallAnnotation(Name = "insertMongo", TccHandlerClass = MongoServer.class)
    public String insertReturnOrder(ReturnOrder returnOrder){

        List<ReturnOrder> orders = new ArrayList<>();

        orders.add(returnOrder);

        orders.add(new ReturnOrder(returnOrder.getId()+"o",returnOrder.getOther()+"o"));

     //   returnOrderRepository.save(returnOrder);

       // returnOrderRepository.insert(returnOrder);

     //   returnOrderRepository.deleteAll(orders);

        //returnOrderRepository.saveAll(orders);

   //     returnOrderRepository.insert(orders);


   //     returnOrderRepository.deleteById(returnOrder.getId());

        returnOrderRepository.delete(returnOrder);

        int i = 10/0;



        return  returnOrder.toString();

    }



    @DogCallAnnotation(Name = "insertMongo", TccHandlerClass = MongoServer.class)
    public String insertMongo(String value){


        returnOrderRepository.save(new ReturnOrder("110",value));


        List<ReturnOrder> resutl = returnOrderRepository.findAll();


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
