package org.dog.message.zookeeper;

import junit.framework.Assert;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//　  ①测试方法上必须使用@Test进行修饰
//
//        ②测试方法必须使用public void 进行修饰，不能带任何的参数
//
//                ③新建一个源代码目录来存放我们的测试代码，即将测试代码和项目业务代码分开
//
//                ④测试类所在的包名应该和被测试类所在的包名保持一致
//
//                ⑤测试单元中的每个方法必须可以独立测试，测试方法间不能有任何的依赖
//
//                ⑥测试类使用Test作为类名的后缀（不是必须）
//
//                ⑦测试方法使用test作为方法名的前缀（不是必须）
//
//　　1.@Test: 测试方法
//
//        　　　　a)(expected=XXException.class)如果程序的异常和XXException.class一样，则测试通过
//        　　　　b)(timeout=100)如果程序的执行能在100毫秒之内完成，则测试通过
//
//        　　2.@Ignore: 被忽略的测试方法：加上之后，暂时不运行此段代码
//
//        　　3.@Before: 每一个测试方法之前运行
//
//        　　4.@After: 每一个测试方法之后运行
//
//        　　5.@BeforeClass: 方法必须必须要是静态方法（static 声明），所有测试开始之前运行，注意区分before，是所有测试方法
//
//        　　6.@AfterClass: 方法必须要是静态方法（static 声明），所有测试结束之后运行，注意区分 @After
public class ZooKeeperTest {



    @Test
    public void zooKeeperTestConnection() throws Exception {

        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 1000000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println(watchedEvent);
            }
        });

        System.out.println(zooKeeper.getSessionId());
    }


    //zooKeeper 是支持多线程的
    @Test
    public void zooKeeperTest() throws Exception {

        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 1000000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println(watchedEvent);
            }
        });

        ExecutorService service = Executors.newFixedThreadPool(10);

        for (int j = 0; j < 20; j++) {

            service.execute(new Runnable() {
                @Override
                public void run() {

                    try {

                        Random r = new Random();

                        for (int i = 0; i < 10; i++) {

                            Integer randomPath = r.nextInt();

                            String ranPath = "/dog/junit/" + randomPath.toString();

                            Stat stat = zooKeeper.exists(ranPath, false);

                            if(stat == null) {

                                zooKeeper.create(ranPath, "None".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

                            }

                            System.out.println("OK "+ i);
                        }

                    } catch (Exception e) {


                        System.out.println("ERROR    "+e);
                    }


                    System.out.println("OK Thread");
                }
            });
        }


       service.awaitTermination(10, TimeUnit.MINUTES);
    }



    @Test
    public void zooKeeperTestCreatePath() throws Exception {

        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 1000000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println(watchedEvent);
            }
        });


        Random r = new Random();

        for(int i=0;i<100;i++) {

            Integer randomPath = r.nextInt();

            String ranPath = "/dog/junit/" + randomPath.toString();

            Stat stat = zooKeeper.exists(ranPath, false);

            zooKeeper.create(ranPath, "None".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

            zooKeeper.create(ranPath + "/subPath", "None".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }


    }


    @Test
    public void zooKeeperTestTran() throws Exception {

        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 1000000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println(watchedEvent);
            }
        });


        Random r = new Random();

        List<Op> ops = new ArrayList<Op>();

        for(int i=0;i<10;i++) {

            Integer randomPath = r.nextInt();

            String ranPath = "/dog/junit/" + randomPath.toString();

            ops.add(Op.create(ranPath, "None".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));

            ops.add(Op.create(ranPath + "/subPath", "None".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));
        }

        zooKeeper.multi(ops);

    }

    @Test
    public void zooKeeperTestList() throws Exception {

        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 1000000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println(watchedEvent);
            }
        });

        Random r = new Random();

        List<Op> ops = new ArrayList<Op>();


        String ranPath = "/dog/junit/" + r.nextInt(1000);

        ops.add(Op.create(ranPath, "None".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));

        zooKeeper.multi(ops);

        Stat stat = new Stat();

        zooKeeper.getData(ranPath,false,stat);

        System.out.println(stat.getAversion());

        zooKeeper.delete(ranPath,0);

        zooKeeper.create(ranPath, "None".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        List<Op> newPos = new ArrayList<Op>();

        newPos.add( Op.check(ranPath,0));

        newPos.add(Op.create("/dog/hh", "None".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));


        zooKeeper.multi(newPos);
    }



}
