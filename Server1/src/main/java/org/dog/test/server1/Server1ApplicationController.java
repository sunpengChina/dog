package org.dog.test.server1;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.dog.core.annotation.DogTccAnnotation;
import org.dog.core.util.ThreadManager;
import org.dog.test.server1.client.Server2Client;
import org.dog.test.server1.client.Server3Client;
import org.dog.test.server1.client.TranD;
import org.dog.test.server1.service.TransServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Server1ApplicationController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    Server2Client client2;

    @Autowired
    Server3Client client3;


    @RequestMapping("/chain/{id}/{other}")
    @DogTccAnnotation(Name = "chain")
    public String chain(@PathVariable String id, @PathVariable String other) throws Exception {

        ReturnOrder trade = new ReturnOrder(id,other);

        //client2.tran(new TranD(id,other));

         String result =  client2.returnOrder(trade);


        return  result;

    }



}