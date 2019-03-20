package org.dog.message.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;

import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args)throws Exception{

        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 100000,
                new Watcher() {
                    @Override
                    public void process(WatchedEvent watchedEvent) {

                        System.out.println(watchedEvent);
                    }
                });

        String path = "/server1";

        try {


            List<String> childs = zooKeeper.getChildren(path,false);

 //           zooKeeper.create(path,"test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

//            zooKeeper.create(path,"test".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

//            zooKeeper.delete(path,-1);
//
//            zooKeeper.delete(path,-1);

            zooKeeper.exists("/root/server3/server4",false);

        }catch (Exception e){

            KeeperException f = (KeeperException)e;

            System.out.println(e);

        }


        System.out.println("test");
    }
}
