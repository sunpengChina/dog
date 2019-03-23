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

    //13698ms  11165ms 10739ms 10586ms 10433ms   平均:  11325
    @RequestMapping("/noTcc5000")
    public String noTcc5000() throws Exception{


        long startTime = System.currentTimeMillis();

        for(int i=0;i<5000;i++){

            server.noTcc();
        }

        long endTime = System.currentTimeMillis();

        String ret = "未在事务里，5000次调用需要的时间：" + (endTime - startTime) + "ms";

        return  ret;
    }

    //33687ms 33052ms 32851ms 35127ms 35951ms    平均 34133
    @RequestMapping("/singleTcc5000")
    public String singleTcc5000() throws Exception{


        long startTime = System.currentTimeMillis();

        for(int i=0;i<5000;i++){

            /*需要将线程中的事务信息清除，否则会自动把这些事务合并为一个事务*/
            ThreadManager.clearTransaction();

            server.singleTcc();

        }

        long endTime = System.currentTimeMillis();

        //在事务里，100次调用需要的时间
        String ret = "在事务里，5000次调用需要的时间：" + (endTime - startTime) + "ms";

        return  ret;
    }


    //50827ms 48177ms 50974ms 55395ms  43760ms   平均 39631
    @RequestMapping("/chainTcc5000thread")
    public String chainTcc5000thread() throws Exception{

        ExecutorService service = Executors.newFixedThreadPool(10);

        long startTime = System.currentTimeMillis();


        final CountDownLatch examBegin = new CountDownLatch(10);

        for(int i=0;i<10;i++){

            service.execute(new Runnable() {
                                @Override
                                public void run() {

                                    for(int j=0;j < 500;j++) {

                                        try {
                                            ThreadManager.clearTransaction();
                                            server.chainTcc();;
                                        } catch (Exception e) {

                                        }
                                    }

                                    examBegin.countDown();
                                }
                            }

            );
        }


        examBegin.await();
        long endTime = System.currentTimeMillis();

        //在事务里，100次调用需要的时间
        String ret = "10个线程各500次调用TCC链式事务，调用需要的时间：" + (endTime - startTime) + "ms";

        return  ret;
    }

   //6400ms 6454ms  6473ms 6448ms  6519ms        平均 6458
    @RequestMapping("/chainNoTcc5000thread")
    public String chainNoTcc5000thread() throws Exception{

        ExecutorService service = Executors.newFixedThreadPool(10);

        long startTime = System.currentTimeMillis();


        final CountDownLatch examBegin = new CountDownLatch(10);

        for(int i=0;i<10;i++){

            service.execute(new Runnable() {
                                @Override
                                public void run() {


                                    for(int j=0;j < 500;j++) {

                                        try {

                                            server.chainTccNoTran();

                                        } catch (Exception e) {

                                        }
                                    }

                                    examBegin.countDown();
                                }
                            }

            );
        }


        examBegin.await();
        long endTime = System.currentTimeMillis();

        //在事务里，100次调用需要的时间
        String ret = "10个线程各500次调用链式非TCC事务，调用需要的时间：" + (endTime - startTime) + "ms";

        return  ret;
    }

   //36831ms  34298ms   33069ms  35814ms  37818ms  平均 35566
    @RequestMapping("/singleTcc5000thread")
    public String singleTcc5000thread() throws Exception{

        ExecutorService service = Executors.newFixedThreadPool(10);

        long startTime = System.currentTimeMillis();


        final CountDownLatch examBegin = new CountDownLatch(10);

        for(int i=0;i<10;i++){

            service.execute(new Runnable() {
                                @Override
                                public void run() {

                                    for(int j=0;j < 500;j++) {
                                        try {
                                            ThreadManager.clearTransaction();
                                            server.singleTcc();

                                        } catch (Exception e) {

                                        }

                                    }
                                    examBegin.countDown();
                                }
                            }

            );
        }


        examBegin.await();
        long endTime = System.currentTimeMillis();

        //在事务里，100次调用需要的时间
        String ret = "10个线程各500次调用非链式TCC事务，调用需要的时间：，调用需要的时间：" + (endTime - startTime) + "ms";

        return  ret;
    }

    //2909ms  2857ms  2844ms 2924ms 2886ms      平均 2884
    @RequestMapping("/singleNoTcc5000thread")
    public String singleNoTcc5000thread() throws Exception{

        ExecutorService service = Executors.newFixedThreadPool(10);

        long startTime = System.currentTimeMillis();


        final CountDownLatch examBegin = new CountDownLatch(10);

        for(int i=0;i<10;i++){

            service.execute(new Runnable() {
                                @Override
                                public void run() {

                                    for(int j=0;j < 500;j++) {
                                        try {

                                            server.noTcc();

                                        } catch (Exception e) {

                                        }
                                    }

                                    examBegin.countDown();
                                }
                            }

            );
        }


        examBegin.await();
        long endTime = System.currentTimeMillis();

        //在事务里，100次调用需要的时间
        String ret = "10个线程各500次调用非链式非TCC事务，调用需要的时间：，调用需要的时间：" + (endTime - startTime) + "ms";

        return  ret;
    }


}