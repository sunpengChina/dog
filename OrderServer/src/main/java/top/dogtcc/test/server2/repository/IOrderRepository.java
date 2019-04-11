package top.dogtcc.test.server2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.dogtcc.test.server2.dao.Orderdao;

public interface IOrderRepository  extends JpaRepository<Orderdao, Integer> {

}

