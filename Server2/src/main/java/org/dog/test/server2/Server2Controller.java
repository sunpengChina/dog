package org.dog.test.server2;

import java.util.concurrent.atomic.AtomicLong;

import org.dog.test.server2.client.Server3Client;
import org.dog.test.server2.client.TranD;
import org.dog.test.server2.dao.MysqlServer;
import org.dog.test.server2.dao.ReturnOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Server2Controller {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    MysqlServer mysqlServer;


    @Autowired
    Server3Client client3;


    @RequestMapping("/hello")
    public String greeting() {
        return "hello/server2";
    }

    @RequestMapping("/tran")
    public String tran(@RequestBody TranD trans) {

        return "OK";

    }

    @RequestMapping("/returnOrder")
    public String tran(@RequestBody ReturnOrder returnOrder) {

        mysqlServer.insertReturnOrder(returnOrder);

        return "OK";
    }


}