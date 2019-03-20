package org.dog.message.zookeeper;


import org.dog.core.common.Connectable;
import org.dog.core.entry.DogTcc;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;
import org.dog.core.jms.exception.NotStartTransactionException;
import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.Closeable;
import java.io.IOException;


public class ConnectableMessage implements Connectable, Closeable {

    private static Logger logger = Logger.getLogger(ConnectableMessage.class);

    protected PathHelper pathHelper;

    protected volatile ZooKeeper zooKeeper;

    protected ZookeeperConfig zoolkeepconfig;

    protected String applicationName;

    protected  final static int AnyVersion = -1;

    public ConnectableMessage(String applicationName, ZookeeperConfig zoolkeepconfig) {

        this.zoolkeepconfig = zoolkeepconfig;

        this.applicationName = applicationName;

        this.pathHelper =  new PathHelper(zoolkeepconfig.getPath(),applicationName);
    }


    protected  void throwException(Exception e)throws ConnectException,InterruptedException {

        if (e instanceof KeeperException) {

            throw new ConnectException();

        }

        if (e instanceof InterruptedException) {

            throw (InterruptedException) e;
        }

    }


    protected void checkIfTransactionStarter(DogTcc transaction) throws NotStartTransactionException {

        if(!transaction.getApplication().equals(applicationName)){

            throw  new NotStartTransactionException();
        }

    }

    protected void checkContent(String content,boolean create,byte[] data) throws ConnectException, NonexistException,InterruptedException{

        Stat stat = null;

        try {

            stat = zooKeeper.exists(content, false);

        }catch (Exception e){

            logger.error(e);

            throwException(e);

        }

        if (stat != null) {



        }else if(create){

            try {

                if(data == null){

                    zooKeeper.create(content,"NONE".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

                }else {

                    zooKeeper.create(content,data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }

            }catch (Exception e){


                logger.error(e);

                throwException(e);
            }

            logger.info("创建：" + content);

        }else{

            throw  new NonexistException();
        }

    }


    public void close() throws IOException{

        try {

            zooKeeper.close();

        }catch (InterruptedException e){


            logger.error(e);

            throw new IOException();
        }

    }

    @Override
    public void connect() throws ConnectException, InterruptedException {

        try{

            zooKeeper = new ZooKeeper(zoolkeepconfig.getConnectString(), zoolkeepconfig.getSessionTimeout(), new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {

                    logger.info(watchedEvent);

                    if(watchedEvent.getState().equals( Event.KeeperState.Expired) || watchedEvent.getState().equals(Event.KeeperState.Disconnected)){

                        try {

                            logger.info("try Reconnect");

                            zooKeeper.close();

                            zooKeeper = new ZooKeeper(zoolkeepconfig.getConnectString(), zoolkeepconfig.getSessionTimeout(),this);

                        }catch (Exception e){

                            logger.info(e);
                        }

                    }

                }
            });

        }catch (IOException e){

            logger.error(e);

            throw new ConnectException();

        }

        try{

            checkContent(pathHelper.zookeeperWorkPath(),false,null);

        }catch (ConnectException|NonexistException|InterruptedException e){

            logger.error(e);

            throwException(e);

        }

        try{

            checkContent(pathHelper.applicationPath(),true,null);

        }catch (ConnectException|NonexistException|InterruptedException e){

            logger.error(e);

            throwException(e);

        }

    }


}
