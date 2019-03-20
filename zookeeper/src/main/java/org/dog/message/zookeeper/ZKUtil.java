package org.dog.message.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;
import java.util.Map;

public class ZKUtil {


    /**
     * 删除子节点
     * @param path
     * @param zk
     * @return
     * @throws InterruptedException
     * @throws KeeperException
     */
    public static boolean rmr(String path, ZooKeeper zk) throws InterruptedException, KeeperException {
        //看看传入的节点是否存在
        if((zk.exists(path, false)) != null) {
            //查看该节点下是否还有子节点
            List<String> children = zk.getChildren(path, false);
            //如果没有子节点，直接删除当前节点
            if(children.size() == 0) {

                zk.delete(path, -1);

            }else {

                //如果有子节点，则先遍历删除子节点
                for(String child : children) {
                    rmr(path+"/"+child,zk);
                }
                //删除子节点之后再删除之前子节点的父节点
                rmr(path,zk);
            }
            return true;
        }else {
            //如果传入的路径不存在直接返回不存在
            System.out.println(path+" not exist");
            return false;
        }

    }

}