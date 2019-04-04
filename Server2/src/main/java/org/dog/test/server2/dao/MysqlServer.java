package org.dog.test.server2.dao;

import org.dog.core.annotation.DogCallAnnotation;
import org.dog.database.core.DbTccHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MysqlServer extends DbTccHandler {


    @Autowired
    private ReturnOrderJpaRepository returnOrderRepository;



    @DogCallAnnotation(Name = "insertMongo", TccHandlerClass = MysqlServer.class)
    public String insertReturnOrder(ReturnOrder returnOrder){


        List<ReturnOrder> orders = new ArrayList<>();

        ReturnOrder other = new ReturnOrder(returnOrder.getId()+"o",returnOrder.getOther()+"o");

        orders.add(returnOrder);

        orders.add(other);

       // returnOrderRepository.save(returnOrder);

        //returnOrderRepository.saveAll(orders);

   //      returnOrderRepository.deleteById(returnOrder.getId());

     //   returnOrderRepository.delete(returnOrder);

     //   returnOrderRepository.deleteInBatch(orders);

        returnOrderRepository.saveAndFlush(returnOrder);

        int i = 1/0;


        return  returnOrder.toString();

    }


}
