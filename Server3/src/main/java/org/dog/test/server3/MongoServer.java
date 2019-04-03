package org.dog.test.server3;

import org.dog.core.annotation.TccHandler;
import org.dog.core.annotation.DogCallAnnotation;
import org.dog.database.core.DbTccHandler;
import org.dog.test.server3.dao.Product;
import org.dog.test.server3.dao.ProductRepository;
import org.dog.test.server3.dao.ReturnOrder;
import org.dog.test.server3.dao.ReturnOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;


import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Component
public class MongoServer extends DbTccHandler {


    @Autowired
    private ReturnOrderRepository returnOrderRepository;

    @Autowired
    private ProductRepository productRepository;


    @DogCallAnnotation(Name = "insertMongo", TccHandlerClass = MongoServer.class)
    public String insertReturnOrder(ReturnOrder returnOrder){


        List<ReturnOrder> orders = new ArrayList<>();

        ReturnOrder other = new ReturnOrder(returnOrder.getId()+"o",returnOrder.getOther()+"o");

        orders.add(returnOrder);

        orders.add(other);

     //   returnOrderRepository.save(returnOrder);

  //      returnOrderRepository.insert(returnOrder);

    //    returnOrderRepository.insert(other);

     //   returnOrderRepository.deleteAll(orders);

        //returnOrderRepository.saveAll(orders);

   //     returnOrderRepository.insert(orders);


   //     returnOrderRepository.deleteById(returnOrder.getId());

  //      returnOrderRepository.delete(returnOrder);

        //ReturnOrder result = returnOrderRepository.findByIdAndOther(returnOrder.getId(),returnOrder.getOther());

     //   returnOrderRepository.deleteByIdAndOther(returnOrder.getId(),returnOrder.getOther());

      //  productRepository.save(new Product("115","商家2",110));

     //   productRepository.insert(new Product(returnOrder.getId(),"商家2",100));

        // productRepository.deleteByNameAndVender(returnOrder.getId(),"商家2");

       //  productRepository.insert(new Product(returnOrder.getId(),"商家2",200));

        productRepository.save(new Product(returnOrder.getId(),"商家2",200));

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
