package top.dogtcc.database.spring.jpa;


import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.dogtcc.core.util.SpringContextUtil;
import top.dogtcc.database.core.annotation.DogDb;
import top.dogtcc.database.core.annotation.OperationType;
import top.dogtcc.database.core.annotation.QueryArg;

import java.util.List;
import java.util.Optional;


public abstract class DogJpaRepository<T, ID> implements JpaRepository<T,ID> {

    @Override
    public List<T> findAll() {
        return repository().findAll();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return repository().findAll(sort);
    }

    @Override
    public List<T> findAllById(Iterable<ID> iterable) {
        return repository().findAllById(iterable);
    }

    @Override
    public void flush() {
          repository().flush();
    }

    @Override
    public void deleteAllInBatch() {
          repository().deleteAllInBatch();
    }

    @Override
    public T getOne(ID id) {
        return repository().getOne(id);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        return  repository().findAll(example);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        return repository().findAll(example,sort);
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
    public long count() {
        return repository().count();
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
        return repository().count();
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        return repository().exists(example);
    }

    public abstract JpaRepository<T,ID> repository();


    /**
     * 自生成主键
     * @param s
     * @param
     * @param <S>
     * @return
     */
    public <S extends T> S create(S s) {

        S ret  = repository().saveAndFlush(s);

        DogJpaRepository<T, ID> self = ( DogJpaRepository<T, ID>) SpringContextUtil.getApplicationContext().getBean(this.getClass());

        self.nullInsert(ret);

        return  ret;
    }

    @DogDb(queryMethodName = "findById",
            saveMethodName = "saveAndFlush", operationType = OperationType.INSERTNEWDATA,deleteMethodName = "deleteById")
    public <S extends T> S nullInsert(S s) {

        return null;

    }


    /**
     * 插入非自生成主键的数据
     * @param s
     * @param <S>
     * @return
     */
    @DogDb(queryMethodName = "findById",
            saveMethodName = "saveAndFlush", operationType = OperationType.INSERTNEWDATA,deleteMethodName = "deleteById")
    public <S extends T> S insert(S s) {

       return repository().save(s);

    }


    /**
     * 修改数据
     * @param s
     * @param <S>
     * @return
     */
    @Override
    @DogDb(queryMethodName = "findById",saveMethodName = "saveAndFlush")
    public <S extends T> S save(S s) {
        return repository().save(s);
    }


    @Override
    public void deleteAll(Iterable<? extends T> iterable) {
        repository().deleteAll(iterable);
    }

    @Override
    public void deleteAll() {
        repository().deleteAll();
    }

    @Override
    @DogDb(queryMethodName = "findById",saveMethodName = "saveAndFlush")
     public  <S extends T> List<S> saveAll(Iterable<S> var1){
       return  repository().saveAll(var1);
    }

    @Override
    @DogDb( queryMethodName = "findById",saveMethodName = "saveAndFlush")
    public void deleteById(@QueryArg(argName = "ID") ID var1){
        repository().deleteById(var1);
    }

    @Override
    @DogDb( queryMethodName = "findById",saveMethodName = "saveAndFlush")
    public void delete(T var1){
        repository().delete(var1);
    }

    @Override
    @DogDb( queryMethodName = "findById",saveMethodName = "saveAndFlush")
    public  <S extends T> S saveAndFlush(S var1){
      return   repository().saveAndFlush(var1);
    }

    @Override
    @DogDb( queryMethodName = "findById",saveMethodName = "saveAndFlush")
    public void deleteInBatch(Iterable<T> var1){
        repository().deleteInBatch(var1);
    }


}
