package top.dogtcc.test.server2.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import top.dogtcc.database.spring.jpa.DogJpaRepository;
import top.dogtcc.test.server2.dao.Orderdao;

@Component
public  class OrderRepository extends DogJpaRepository<Orderdao, Integer> {

    @Autowired
    IOrderRepository repository;


    @Override
     public JpaRepository<Orderdao, Integer> repository(){
         return      repository;
     }

}


