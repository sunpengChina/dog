package top.dogtcc.message.zookeeper;


import top.dogtcc.core.common.Connectable;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.jms.exception.ConnectException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import top.dogtcc.message.zookeeper.connection.ConnectionPool;
import top.dogtcc.message.zookeeper.exception.NoApplicationWorkPathException;
import top.dogtcc.message.zookeeper.exception.NoNodeException;
import top.dogtcc.message.zookeeper.exception.NoZookeeperWorkPathException;
import top.dogtcc.message.zookeeper.exception.NotStartTccNodeException;
import top.dogtcc.message.zookeeper.util.PathHelper;
import top.dogtcc.message.zookeeper.util.ZkHelp;

import java.io.Closeable;
import java.io.IOException;


public class ConnectableMessage implements Connectable, Closeable {

    private static Logger logger = Logger.getLogger(ConnectableMessage.class);

    protected PathHelper pathHelper;

    public ZooKeeper getConnection(){

        return  connectionPool.getZooKeeper();
    }


    protected ZookeeperConfig zoolkeepconfig;

    protected String applicationName;

    protected  final static int AnyVersion = -1;

    private ConnectionPool connectionPool;

    public ConnectableMessage(String applicationName, ZookeeperConfig zoolkeepconfig) {

        this.zoolkeepconfig = zoolkeepconfig;

        this.applicationName = applicationName;

        this.pathHelper =  new PathHelper(zoolkeepconfig.getPath(),applicationName);

        this.connectionPool = new ConnectionPool(zoolkeepconfig.getPoolsize(),zoolkeepconfig.getConnectString(),zoolkeepconfig.getSessionTimeout());
    }


    protected void checkIfTransactionStarter(DogTcc transaction) throws NotStartTccNodeException {

        if(!transaction.getApplication().equals(applicationName)){

            throw  new NotStartTccNodeException();
        }

    }


    public void close() throws IOException{

        connectionPool.close();

    }

    @Override
    public void connect() throws ConnectException, InterruptedException {


        connectionPool.connect();


        try{

            ZkHelp.checkContent(getConnection(),pathHelper.zookeeperWorkPath(),false,null);

        }catch (NoNodeException e){

            throw new NoZookeeperWorkPathException();

        }

        try{

            ZkHelp.checkContent(getConnection(),pathHelper.applicationPath(),true,null);

        }catch(NoNodeException e){

            throw new NoApplicationWorkPathException();

        }

    }


}
