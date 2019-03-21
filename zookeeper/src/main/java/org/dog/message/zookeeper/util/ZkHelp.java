package org.dog.message.zookeeper.util;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class ZkHelp {

    private static Logger logger = Logger.getLogger(ZkHelp.class);


    private static Set<String> haveCheckedPath = new ConcurrentSkipListSet<>();


    public static   void throwException(Exception e)throws ConnectException,InterruptedException {

        if (e instanceof KeeperException) {

            throw new ConnectException();

        }

        if (e instanceof InterruptedException) {

            throw (InterruptedException) e;
        }

    }



    public static void checkContent(ZooKeeper zooKeeper,String content,boolean create,byte[] data) throws ConnectException, NonexistException,InterruptedException{

        if(haveCheckedPath.size()>100000){

            haveCheckedPath.clear();

        }

        /**
         * 有该目录了，无需检测
         */
        if(haveCheckedPath.contains(content)){

            return;
        }

        Stat stat = null;

        try {

            stat = zooKeeper.exists(content, false);

        }catch (Exception e){

            logger.error(e);

            throwException(e);

        }

        if (stat != null) {

            haveCheckedPath.add(content);

        }else if(create){

            try {

                if(data == null){

                    zooKeeper.create(content,"NONE".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

                }else {

                    zooKeeper.create(content,data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }

                haveCheckedPath.add(content);

            }catch (Exception e){


                logger.error(e);

                throwException(e);
            }

            logger.info("创建：" + content);

        }else{

            throw  new NonexistException();
        }

    }


}