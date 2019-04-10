package top.dogtcc.database.spring.mongo;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import top.dogtcc.database.core.annotation.DogDb;
import top.dogtcc.database.core.annotation.OperationType;
import top.dogtcc.database.core.annotation.QueryArg;

import java.util.List;
import java.util.Optional;

public abstract class DogMongoRepository<T, ID> implements MongoRepository<T,ID> {
    @Override
    public List<T> findAll() {
        return repository().findAll();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return repository().findAll(sort);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        return repository().findAll(example);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        return repository().findAll(example, sort);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository().findAll(pageable);
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository().findById(id);
    }

    @Override
    public boolean existsById(ID id) {
        return repository().existsById(id);
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> iterable) {
        return repository().findAllById(iterable);
    }

    @Override
    public long count() {
        return repository().count();
    }

    @Override
    public void deleteAll() {
        repository().deleteAll();
    }

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        return repository().findOne(example);
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        return repository().findAll(example,pageable);
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        return repository().count(example);
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        return repository().exists(example);
    }

    public abstract MongoRepository<T,ID> repository();

    @Override
    @DogDb(queryMethodName = "findById",saveMethodName = "save")
    public  <S extends T> S save(S s){
        return  repository().save(s);
    }


    @Override
    @DogDb(queryMethodName = "findById",
            saveMethodName = "save", operationType = OperationType.INSERTNEWDATA,deleteMethodName = "deleteById")
    public  <S extends T> S insert(S s){

        return  repository().insert(s);
    }

    @Override
    @DogDb(queryMethodName = "findById",saveMethodName = "save")
    public void deleteAll(Iterable<? extends T> iterable){

         repository().deleteAll(iterable);

    }


    @Override
    @DogDb( queryMethodName = "findById",saveMethodName = "save")
    public <S extends T> List<S> saveAll(Iterable<S> iterable){

        return  repository().saveAll(iterable);
    }


    @Override
    @DogDb( queryMethodName = "findById",saveMethodName = "save", operationType = OperationType.INSERTNEWDATA,deleteMethodName = "deleteById")
    public <S extends T> List<S> insert(Iterable<S> iterable){

        return  repository().insert(iterable);
    }

    @Override
    @DogDb( queryMethodName = "findById",saveMethodName = "save")
    public void deleteById(@QueryArg(argName = "ID") ID id){
        repository().deleteById(id);
    }

    @Override
    @DogDb(queryMethodName = "findById",saveMethodName = "save")
    public  void delete(T t){
        repository().delete(t);
    }
}
