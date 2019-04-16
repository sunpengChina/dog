package top.dogtcc.message.zookeeper.connection;

import org.apache.zookeeper.ZooKeeper;
import top.dogtcc.core.common.Connectable;
import top.dogtcc.core.jms.exception.ConnectException;
import top.dogtcc.core.jms.exception.TccNotExsitException;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConnectionPool implements Connectable, Closeable {

    private ThreadLocal<ZooKeeper> threadLocal;
    private int size;

    private Random r = new Random();

    private List<Connection> connections = new ArrayList<>();

    public ConnectionPool(int size,String connectString,int timeout){

        this.size = size;

        for(int i =0;i<size;i++){

            connections.add(new Connection(connectString,timeout));

        }
    }

    public ZooKeeper getZooKeeper(){
        return  connections.get(r.nextInt(size)).getZooKeeper();
    }

    @Override
    public synchronized void close() throws IOException {

        for(Connection connection :connections){

            connection.close();

        }

    }

    @Override
    public synchronized void connect() throws ConnectException, InterruptedException {

        for(Connection connection :connections){

            connection.connect();

        }

    }


}
