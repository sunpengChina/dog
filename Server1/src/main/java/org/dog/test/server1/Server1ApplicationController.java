package org.dog.test.server1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.dog.core.util.ThreadManager;
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

      @RequestMapping("/timenotran")
    public String timenotranTest() throws Exception{


        long startTime = System.currentTimeMillis();

        for(int i=0;i<1000;i++){

            server.nottccTran();
        }

        long endTime = System.currentTimeMillis();

        //未在事务里，1000次调用需要的时间：9707ms

        String ret = "未在事务里，1000次调用需要的时间：" + (endTime - startTime) + "ms";

        return  ret;
    }

    @RequestMapping("/timeintran")
    public String timeintranTest() throws Exception{


        long startTime = System.currentTimeMillis();

        for(int i=0;i<100;i++){

            server.tccTran();

            /*需要将线程中的事务信息清除，否则会自动把这些事务合并为一个事务*/
            ThreadManager.clearTransaction();
        }

        long endTime = System.currentTimeMillis();

        //在事务里，100次调用需要的时间
        String ret = "在事务里，100次调用需要的时间：" + (endTime - startTime) + "ms";

        return  ret;
    }

    @RequestMapping("/threadintran")
    public String threadintran() throws Exception{

        ExecutorService service = Executors.newFixedThreadPool(10);

        long startTime = System.currentTimeMillis();

        for(int i=0;i<100;i++){

            service.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        ThreadManager.clearTransaction();
                                        server.tccTran();
                                        //这个线程可能会被别人使用
                                        ThreadManager.clearTransaction();
                                    }catch (Exception e){

                                    }
                                }
                            }

            );
        }


        long endTime = System.currentTimeMillis();

        //在事务里，100次调用需要的时间
        String ret = "100个线程在大小为10的线程池中执行事务，调用需要的时间：" + (endTime - startTime) + "ms";

        return  ret;
    }


}