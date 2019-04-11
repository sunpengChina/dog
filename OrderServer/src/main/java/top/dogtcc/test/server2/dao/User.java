package top.dogtcc.test.server2.dao;

import lombok.Data;
import top.dogtcc.database.core.annotation.DogTable;
import top.dogtcc.database.core.annotation.QueryArg;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@DogTable(tableName = "User",dbName = "dbname")
@Entity
@Table(name="User")
public class User {

    @QueryArg(argName = "ID")
    @Id
    private String userId;

    private String userName;
}
