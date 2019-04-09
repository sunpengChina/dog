package top.dogtcc.database.spring.jpa;

import top.dogtcc.database.core.annotation.DogDb;
import top.dogtcc.database.core.annotation.QueryArg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DogJpaRepository  <T, ID>extends JpaRepository<T,ID> {

    @Override
    @DogDb(repositoryClass = DogJpaRepository.class, queryMethodName = "findById",saveMethodName = "saveAndFlush")
    <S extends T> S save(S var1);

    @Override
    @DogDb(repositoryClass = DogJpaRepository.class, queryMethodName = "findById",saveMethodName = "saveAndFlush")
    <S extends T> List<S> saveAll(Iterable<S> var1);

    @Override
    @DogDb(repositoryClass = DogJpaRepository.class, queryMethodName = "findById",saveMethodName = "saveAndFlush")
    void deleteById(@QueryArg(argName = "ID") ID var1);

    @Override
    @DogDb(repositoryClass = DogJpaRepository.class, queryMethodName = "findById",saveMethodName = "saveAndFlush")
    void delete(T var1);

    @Override
    @DogDb(repositoryClass = DogJpaRepository.class, queryMethodName = "findById",saveMethodName = "saveAndFlush")
    void deleteAll(Iterable<? extends T> var1);

    @Override
    @DogDb(repositoryClass = DogJpaRepository.class, queryMethodName = "findById",saveMethodName = "saveAndFlush")
    <S extends T> S saveAndFlush(S var1);

    @Override
    @DogDb(repositoryClass = DogJpaRepository.class, queryMethodName = "findById",saveMethodName = "saveAndFlush")
    void deleteInBatch(Iterable<T> var1);

    void deleteAllInBatch();

    void deleteAll();

    void flush();

}
