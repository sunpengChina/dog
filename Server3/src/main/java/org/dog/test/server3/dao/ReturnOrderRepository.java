package org.dog.test.server3.dao;

import org.dog.database.spring.mongo.DogMongoRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.data.repository.CrudRepository;

public interface ReturnOrderRepository extends DogMongoRepository<ReturnOrder, String> {


}
