package org.dog.test.server3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class Server3Controller {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    MongoServer mongoServer;

    @Autowired
    MysqlServer mysqlServer;


    @RequestMapping("/hello")
    public String greeting() {
        return "hello/server3";
    }

    @RequestMapping("/tran")
    public String tran(@RequestBody TranD trans) {

        mongoServer.insertMongo(trans.getValue1());

        mysqlServer.insertMysql(trans.getValue2());

        return "OK";
    }

}