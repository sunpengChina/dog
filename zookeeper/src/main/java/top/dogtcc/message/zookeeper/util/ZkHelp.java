package top.dogtcc.message.zookeeper.util;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.jms.exception.CallNotExsitException;
import top.dogtcc.core.jms.exception.ConnectException;
import top.dogtcc.core.jms.exception.TccNotExsitException;
import top.dogtcc.message.zookeeper.exception.NoNodeException;


import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class ZkHelp {

    private static Logger logger = Logger.getLogger(ZkHelp.class);


    private static Set<String> haveCheckedPath = new ConcurrentSkipListSet<>();



    public static boolean exist(ZooKeeper zooKeeper, String content) throws ConnectException, InterruptedException {

        try {

            return zooKeeper.exists(content, false) != null;

        }catch (KeeperException e){

            throw   new ConnectException();

        }

    }


    public static void checkContent(ZooKeeper zooKeeper, String content, boolean create, byte[] data) throws ConnectException, InterruptedException {

        if (haveCheckedPath.size() > 100000) {

            haveCheckedPath.clear();

        }

        /**
         * 有该目录了，无需检测
         */
        if (haveCheckedPath.contains(content)) {

            return;
        }

        Stat stat = null;

        try {

            stat = zooKeeper.exists(content, false);

        } catch (KeeperException e) {

            throw new ConnectException(e);

        }

        if (stat != null) {

            haveCheckedPath.add(content);

        } else if (create) {

            try {

                if (data == null) {

                    zooKeeper.create(content, "NONE".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

                } else {

                    zooKeeper.create(content, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }

                haveCheckedPath.add(content);

            } catch (KeeperException e) {

                if(e instanceof  KeeperException.NoNodeException){

                    throw  new NoNodeException();

                }else {

                    throw  new ConnectException();
                }

            }


        } else {

            throw new NoNodeException();
        }

    }


}