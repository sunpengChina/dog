package org.dog.database.spring.mongo;
import org.dog.database.core.annotation.DogDb;
import org.dog.database.core.annotation.OperationType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DogMongoRepository <T, ID> extends MongoRepository<T, ID> {


    @Override
    @DogDb(queryClass = DogMongoRepository.class, queryMethodName = "findById",saveMethodName = "save")
    <S extends T> S save(S s);

    @Override
    @DogDb(queryClass = DogMongoRepository.class, queryMethodName = "findAllById",saveMethodName = "save")
    void deleteAll(Iterable<? extends T> iterable);


    @Override
    @DogDb(queryClass = DogMongoRepository.class, queryMethodName = "findById",saveMethodName = "save")
    <S extends T> List<S> saveAll(Iterable<S> iterable);

    @Override
    @DogDb(queryClass = DogMongoRepository.class, queryMethodName = "findById",saveMethodName = "save")
    <S extends T> S insert(S s);

    @Override
    @DogDb(queryClass = DogMongoRepository.class, queryMethodName = "findById",saveMethodName = "save")
    <S extends T> List<S> insert(Iterable<S> iterable);


    @Override
    @DogDb(queryClass = DogMongoRepository.class, queryMethodName = "findById",saveMethodName = "save")
    void deleteById(ID id);

    @Override
    @DogDb(queryClass = DogMongoRepository.class, queryMethodName = "findById",saveMethodName = "save")
    void delete(T t);

    @Override
    @DogDb(queryClass = DogMongoRepository.class, queryMethodName = "findAll",saveMethodName = "save",type = OperationType.DELETEALL)
    void deleteAll();
}
