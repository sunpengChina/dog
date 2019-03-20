package com.server1.application;

import java.util.concurrent.atomic.AtomicLong;

import com.server1.application.service.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Server1Controller {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    Server server;


    @RequestMapping("/hello")
    public String hello() throws Exception {
        return  "hello/server1";
    }


    @RequestMapping("/tran")
    public String tran() throws Exception {
        return  server.getValueFromServer2();
    }


}