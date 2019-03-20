package org.dog.test.server1;

import java.util.concurrent.atomic.AtomicLong;

import org.dog.test.server1.service.TransServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Server1ApplicationController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    TransServer server;


    @RequestMapping("/hello")
    public String hello() throws Exception {
        return  "hello/server1";
    }


    @RequestMapping("/tran")
    public String tran() throws Exception {
        return  server.tccTran();
    }


}