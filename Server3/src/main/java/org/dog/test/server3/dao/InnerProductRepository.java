package org.dog.test.server3.dao;

import org.dog.database.core.annotation.QueryArg;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InnerProductRepository extends MongoRepository<Product,String> {

    void deleteByNameAndVender( String name,  String vender);

    Product findByNameAndVender(String name,String vender);

}