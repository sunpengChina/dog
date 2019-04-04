package org.dog.test.server3.dao;

import org.dog.database.core.annotation.DogDb;
import org.dog.database.core.annotation.QueryArg;
import org.dog.database.spring.mongo.DogMongoRepository;
//import org.springframework.data.repository.CrudRepository;

public interface ReturnMongoRepository extends DogMongoRepository<ReturnOrder, String> {


     ReturnOrder findByIdAndOther(String id, String other) ;

     @DogDb(repositoryClass = ReturnMongoRepository.class, queryMethodName = "findByIdAndOther",
             saveMethodName = "save")
     void deleteByIdAndOther(@QueryArg(argName = "ID") String id, @QueryArg(argName = "Other")String other);


}
