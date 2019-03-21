package org.dog.message.zookeeper;

import org.apache.zookeeper.Op;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.DogTccStatus;
import org.dog.core.entry.DogCall;
import org.dog.core.entry.DogCallStatus;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;
import org.dog.core.jms.exception.NotStartTransactionException;
import org.dog.core.jms.IBroker;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.dog.message.zookeeper.util.ZkHelp;

import java.util.ArrayList;
import java.util.List;

import static org.dog.message.zookeeper.util.ZkHelp.throwException;


public abstract class SimultaneousMessage extends ConnectableMessage implements IBroker {

    private static Logger logger = Logger.getLogger(SimultaneousMessage.class);

    public SimultaneousMessage(String applicationName, ZookeeperConfig autoConfig) {

        super(applicationName, autoConfig);

    }


    /**
     * 注册TCC事务
     * @param tcc
     * @throws ConnectException
     * @throws NonexistException
     * @throws InterruptedException
     */
    @Override
    public synchronized void  registerTcc(DogTcc tcc) throws ConnectException,NonexistException,InterruptedException{


        ZkHelp.checkContent(getConnection(),pathHelper.tccNamePath(tcc),true,null);

        List<Op> ops = new ArrayList<Op>();

        ops.add(Op.create(pathHelper.tccKeyPath(tcc), DogTccStatus.TRY.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));

        ops.add(Op.create(pathHelper.tccNodesPath(tcc), "NONE".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));

        ops.add(Op.create(pathHelper.tccMonitorContent(tcc), "NONE".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL));


        try {

            getConnection().multi(ops);

            logger.info(tcc +"Start OK");

        }catch (Exception e){

            logger.error(tcc +"Start error" + e);
        }


    }

    @Override
    public synchronized void confirmTry(DogTcc tcc) throws NotStartTransactionException,ConnectException ,InterruptedException{

        checkIfTransactionStarter(tcc);

        try{

            getConnection().setData(pathHelper.tccKeyPath(tcc), DogTccStatus.CONFIRM.getBytes(),AnyVersion);

            logger.info(tcc + ": confirm");

        }catch (KeeperException|InterruptedException e){

            logger.error(e);

            throwException(e);
        }

    }

    @Override
    public synchronized void cancelTry(DogTcc tcc) throws NotStartTransactionException,ConnectException ,InterruptedException{

        checkIfTransactionStarter(tcc);

        try {

            getConnection().setData(pathHelper.tccKeyPath(tcc), DogTccStatus.CANCEL.getBytes(),AnyVersion);

            logger.info(tcc + ": cancel");

        }catch (KeeperException|InterruptedException e){

            logger.error(e);

            throwException(e);
        }


    }

    @Override
    public synchronized void registerCall(DogTcc transaction, DogCall call, byte[] data) throws ConnectException,InterruptedException ,NonexistException {


        ZkHelp.checkContent(getConnection(),pathHelper.subApplicationPath(transaction,applicationName),true,null);

        List<Op> ops = new ArrayList<Op>();

        ops.add(Op.create(pathHelper.callPath(transaction,applicationName,call),data,ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));

        ops.add(Op.create(pathHelper.callMonitorPath(transaction,applicationName,call),null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL));

        try {

            getConnection().multi(ops);

            logger.info(transaction +" " +"Start Call:" + call +" OK");

        }catch (Exception e){

            logger.info(transaction +" " +"Start Call:" + call +" error");
        }



    }

    @Override
    public synchronized void confirmCall(DogTcc transaction, DogCall call) throws ConnectException, InterruptedException {

        try {

            List<Op> ops = new ArrayList<Op>();

            ops.add(Op.delete(pathHelper.callMonitorPath(transaction,applicationName,call),AnyVersion));

            ops.add(Op.delete(pathHelper.callPath(transaction,applicationName,call),AnyVersion));

            getConnection().multi(ops);

            logger.info( transaction + "  " +call +" confirm");


        }catch (Exception e){

            logger.error("删除时，被其他节点托管！，将由其他节点删除！");

            throwException(e);
        }


    }

    @Override
    public synchronized void clearTcc(DogTcc transaction) throws ConnectException, InterruptedException{

        try {

            List<Op> ops = new ArrayList<Op>();

            ops.add(Op.delete(pathHelper.tccMonitorContent(transaction),AnyVersion));

            ops.add(Op.delete(pathHelper.tccKeyPath(transaction),AnyVersion));

            getConnection().multi(ops);

            logger.info(transaction + " clear");

        }catch (KeeperException|InterruptedException  e){

            logger.error(e);

            throwException(e);
        }

    }

}
