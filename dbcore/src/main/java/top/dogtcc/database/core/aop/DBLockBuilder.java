package top.dogtcc.database.core.aop;

import top.dogtcc.core.entry.TccLock;
import top.dogtcc.database.core.annotation.DogDb;
import top.dogtcc.database.core.annotation.DogTable;
import top.dogtcc.database.core.annotation.QueryArg;

import java.util.List;


public class DBLockBuilder {

    private String  dbName = "";

    private String tableName = "";

    private String header = "";

    public DBLockBuilder() {

    }

    public DBLockBuilder setDogDb(DogDb dogDb){

        if(!dogDb.dbName().equals("")){

            this.dbName = dogDb.dbName();
        }

        if(!dogDb.tableName().equals("")){

            this.tableName = dogDb.tableName();
        }


        return  this;
    }

    public DBLockBuilder setQueryArgs(List<QueryArg> args){

        for(QueryArg arg: args){

            header = header + arg.argName();
        }

        return  this;
    }

    public  DBLockBuilder setDogTable(DogTable dogTable){

        if(!dogTable.dbName().equals("")){

            this.dbName = dogTable.dbName();
        }

        if(!dogTable.tableName().equals("")){

            this.tableName = dogTable.tableName();
        }

        return  this;

    }


    public  TccLock build(List<Object> objects){

        String key = dbName + tableName + header ;

        for(Object obj:objects){

            key = key + obj.toString();
        }

        return  new TccLock(key);
    }


}
