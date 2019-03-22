package org.dog.test.server1;

import java.util.concurrent.CountDownLatch;
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


    @RequestMapping("/chainTcc")
    public String tran() throws Exception {
        return  server.chainTcc();
    }

      @RequestMapping("/noTcc1000")
    public String noTcc1000() throws Exception{


        long startTime = System.currentTimeMillis();

        for(int i=0;i<1000;i++){

            server.noTcc();
        }

        long endTime = System.currentTimeMillis();

        //未在事务里，1000次调用需要的时间：9707ms

        String ret = "未在事务里，1000次调用需要的时间：" + (endTime - startTime) + "ms";

        return  ret;
    }

    @RequestMapping("/singleTcc100")
    public String singleTcc100() throws Exception{


        long startTime = System.currentTimeMillis();

        for(int i=0;i<100;i++){

            server.singleTcc();

            /*需要将线程中的事务信息清除，否则会自动把这些事务合并为一个事务*/
            ThreadManager.clearTransaction();
        }

        long endTime = System.currentTimeMillis();

        //在事务里，100次调用需要的时间
        String ret = "在事务里，100次调用需要的时间：" + (endTime - startTime) + "ms";

        return  ret;
    }


    @RequestMapping("/chainTcc1000thread")
    public String chainTcc1000thread() throws Exception{

        ExecutorService service = Executors.newFixedThreadPool(10);

        long startTime = System.currentTimeMillis();


        final CountDownLatch examBegin = new CountDownLatch(100);

        for(int i=0;i<100;i++){

            service.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        ThreadManager.clearTransaction();
                                        server.chainTcc();
                                        //这个线程可能会被别人使用
                                        ThreadManager.clearTransaction();
                                    }catch (Exception e){

                                    }

                                    examBegin.countDown();
                                }
                            }

            );
        }


        examBegin.await();
        long endTime = System.currentTimeMillis();

        //在事务里，100次调用需要的时间
        String ret = "100个线程在大小为10的线程池中执行多调用TCC事务，调用需要的时间：" + (endTime - startTime) + "ms";

        return  ret;
    }


    @RequestMapping("/singleTcc1000thread")
    public String singleTcc1000thread() throws Exception{

        ExecutorService service = Executors.newFixedThreadPool(10);

        long startTime = System.currentTimeMillis();


        final CountDownLatch examBegin = new CountDownLatch(100);

        for(int i=0;i<100;i++){

            service.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        ThreadManager.clearTransaction();
                                        server.singleTcc();
                                        //这个线程可能会被别人使用
                                        ThreadManager.clearTransaction();
                                    }catch (Exception e){

                                    }

                                    examBegin.countDown();
                                }
                            }

            );
        }


        examBegin.await();
        long endTime = System.currentTimeMillis();

        //在事务里，100次调用需要的时间
        String ret = "100个线程在大小为10的线程池中执行单调用TCC事务，调用需要的时间：" + (endTime - startTime) + "ms";

        return  ret;
    }


}