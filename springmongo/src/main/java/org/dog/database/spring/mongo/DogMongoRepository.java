package org.dog.database.spring.mongo;
import org.dog.database.core.annotation.DogDb;
import org.dog.database.core.annotation.OperationType;
import org.dog.database.core.annotation.QueryArg;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public  interface DogMongoRepository <T, ID> extends MongoRepository<T, ID> {


    @Override
    @DogDb(repositoryClass = DogMongoRepository.class, queryMethodName = "findById",saveMethodName = "save")
    <S extends T> S save(S s);


    @Override
    @DogDb(repositoryClass = DogMongoRepository.class, queryMethodName = "findById",
            saveMethodName = "save", operationType = OperationType.INSERTNEWDATA,deleteMethodName = "deleteById")
    <S extends T> S insert(S s);

    @Override
    @DogDb(repositoryClass = DogMongoRepository.class, queryMethodName = "findById",saveMethodName = "save")
    void deleteAll(Iterable<? extends T> iterable);


    @Override
    @DogDb(repositoryClass = DogMongoRepository.class, queryMethodName = "findById",saveMethodName = "save")
    <S extends T> List<S> saveAll(Iterable<S> iterable);


    @Override
    @DogDb(repositoryClass = DogMongoRepository.class, queryMethodName = "findById",saveMethodName = "save", operationType = OperationType.INSERTNEWDATA,deleteMethodName = "deleteById")
    <S extends T> List<S> insert(Iterable<S> iterable);

    @Override
    @DogDb(repositoryClass = DogMongoRepository.class, queryMethodName = "findById",saveMethodName = "save")
    void deleteById(@QueryArg(argName = "ID") ID id);

    @Override
    @DogDb(repositoryClass = DogMongoRepository.class, queryMethodName = "findById",saveMethodName = "save")
    void delete(T t);


    void deleteAll();

}
