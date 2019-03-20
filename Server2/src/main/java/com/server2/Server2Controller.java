package com.server2;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Server2Controller {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    AbstractTryListener server;


    @RequestMapping("/hello")
    public String greeting() {
        return "hello/server2";
    }

    @RequestMapping("/tran")
    public String tran() {

        server.insertMongo("hhh");

        return server.insertToMysql("something");
    }

}