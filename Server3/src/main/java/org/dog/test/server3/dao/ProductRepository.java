package org.dog.test.server3.dao;

import com.mongodb.client.result.UpdateResult;
import org.dog.database.core.annotation.DogDb;
import org.dog.database.core.annotation.OperationType;
import org.dog.database.core.annotation.QueryArg;
import org.dog.database.spring.mongo.DogMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;


@Component
public class ProductRepository{

    @Autowired
    private InnerProductRepository innerProductRepository;

    @Autowired
    private MongoTemplate mongoTemplate;


    @DogDb(queryClass = ProductRepository.class, queryMethodName = "findByNameAndVender",
            saveMethodName = "save",dbName = "dbname",tableName = "Product")
   public void save(Product var1){

        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.andOperator(
                Criteria.where("name").is(var1.getName()), Criteria.where("vender").is(var1.getVender()));
        query.addCriteria(criteria);
        Update update = Update.update("price",var1.getPrice());
        UpdateResult result = mongoTemplate.upsert(query,update,Product.class);

    }


       Product findByNameAndVender(String name,String vender){
       return innerProductRepository.findByNameAndVender(name,vender);
    }


    @DogDb(queryClass = ProductRepository.class, queryMethodName = "findByNameAndVender",
            saveMethodName = "save",dbName = "dbname",tableName = "Product")
   public void deleteByNameAndVender(@QueryArg(argName = "name") String name,@QueryArg(argName = "vender")String vender){
        innerProductRepository.deleteByNameAndVender(name,vender);
    }



    @DogDb(queryClass = ProductRepository.class, queryMethodName = "findByNameAndVender",
            saveMethodName = "save",type = OperationType.INSERTNEWDATA,deleteMethodName = "deleteByNameAndVender")
    public Product  insert(Product s){
       return innerProductRepository.insert(s);
    }



}
