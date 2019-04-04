package org.dog.test.server3;

import org.dog.core.annotation.DogCallAnnotation;
import org.dog.database.core.DbTccHandler;
import org.dog.test.server3.dao.ReturnOrder;
import org.dog.test.server3.dao.ReturnMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;

@Component
public class MongoServer extends DbTccHandler {


    @Autowired
    private ReturnMongoRepository returnOrderRepository;



    @DogCallAnnotation(Name = "insertMongo", TccHandlerClass = MongoServer.class)
    public String insertReturnOrder(ReturnOrder returnOrder){


        List<ReturnOrder> orders = new ArrayList<>();

        ReturnOrder other = new ReturnOrder(returnOrder.getId()+"o",returnOrder.getOther()+"o");

        orders.add(returnOrder);

        orders.add(other);

       //  returnOrderRepository.insert(returnOrder);
       //  returnOrderRepository.save(returnOrder);
       //  returnOrderRepository.deleteAll(orders);
        //  returnOrderRepository.saveAll(orders);
       //  returnOrderRepository.insert(orders);
        // returnOrderRepository.deleteById(returnOrder.getId());
       //  returnOrderRepository.delete(returnOrder);
        // returnOrderRepository.deleteByIdAndOther(other.getId(),other.getOther());


        return  returnOrder.toString();

    }



    @DogCallAnnotation(Name = "insertMongo", TccHandlerClass = MongoServer.class)
    public String insertMongo(String value){


        returnOrderRepository.save(new ReturnOrder("110",value));


        List<ReturnOrder> resutl = returnOrderRepository.findAll();


        System.out.println("insertMongo");

        return  value;

    }

}
