package top.dogtcc.test.server2.dao;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import top.dogtcc.database.core.annotation.DogTable;
import top.dogtcc.database.core.annotation.QueryArg;
import javax.persistence.*;

import javax.persistence.Entity;
import java.io.Serializable;
/**
 * @ClassName: ReturnOrder
 * @Description:
 * @Author: ZhangLingKe
 * @CreateDate: 2019/3/19 11:50
 * @Version: 1.0
 */
@Data
@DogTable(tableName = "Order",dbName = "dbname")
@Entity
public class Orderdao implements Serializable {

    @QueryArg(argName = "ID")
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String userId;

    @Column
    private int goodId;

    @Column
    private int nums;

}
