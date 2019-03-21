package org.dog.message.zookeeper.connection;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.dog.core.common.Connectable;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;

import java.io.Closeable;
import java.io.IOException;

public class Connection implements Watcher, Connectable, Closeable {


    private static Logger logger = Logger.getLogger(Connection.class);

    private volatile ZooKeeper zooKeeper;

    private String connectString;

    private int timeout;


    public Connection(String connectString, int timeout){

        this.connectString = connectString;

        this.timeout = timeout;
    }


    @Override
    public synchronized void process(WatchedEvent watchedEvent) {

        logger.info(watchedEvent);

        if(watchedEvent.getState().equals( Event.KeeperState.Expired) || watchedEvent.getState().equals(Event.KeeperState.Disconnected)){

            try {

                connect();

            }catch (Exception e){

                logger.info(e);
            }

        }

    }

    @Override
    public synchronized void connect() throws ConnectException, NonexistException, InterruptedException {

        try {

            close();

            zooKeeper = new ZooKeeper(connectString,timeout,this);

        }catch (Exception e){

            throw  new ConnectException();
        }
    }

    @Override
    public synchronized void close() throws IOException {

        try{

            if(zooKeeper != null){

                zooKeeper.close();

                zooKeeper = null;
            }

        }catch (Exception e){

            throw  new IOException();
        }

    }

    public ZooKeeper getZooKeeper(){
        return  zooKeeper;
    }
}
