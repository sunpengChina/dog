package org.dog.message.zookeeper;

import org.apache.zookeeper.Op;
import org.apache.zookeeper.data.Stat;
import org.dog.core.entry.*;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.exception.NonexistException;
import org.dog.core.jms.exception.NotStartTransactionException;
import org.dog.core.jms.IBroker;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.dog.core.util.IBytePackConvert;
import org.dog.core.util.ThreadManager;
import org.dog.message.zookeeper.util.ZkHelp;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.dog.message.zookeeper.util.ZkHelp.throwException;


public abstract class SimultaneousMessage extends ConnectableMessage implements IBroker {

    @Autowired
    IBytePackConvert convert;

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
    public synchronized void setCallContext(DogTcc tcc, DogCall call, TccContext context) throws ConnectException, NonexistException, InterruptedException {

        ZkHelp.checkContent(getConnection(),pathHelper.subApplicationPath(tcc,applicationName),false,null);

        List<Op> ops = new ArrayList<Op>();

        ops.add(Op.setData(pathHelper.callPath(tcc,applicationName,call),convert.objectToByteArray(context),AnyVersion));

        try {

            getConnection().multi(ops);

            logger.info(tcc +" " +"设置数据:" + call +" OK");

        }catch (Exception e){

            logger.error(e);

        }

    }

    @Override
    public synchronized Set<TccLock> lock(DogTcc transaction, DogCall call, Set<TccLock> locks)throws ConnectException,InterruptedException ,NonexistException {

        Set<TccLock> newlocks = new HashSet<>();

        TccContext oldContext = ThreadManager.getTccContext();

        List<Op> ops = new ArrayList<Op>();

        for (TccLock lock : locks){

            if(oldContext.getLockList().contains(lock)){

                continue;

            }

            byte[] bytelock = null;

            try {

                bytelock  =  getConnection().getData(pathHelper.lockerPath(lock.getKey()),false,new Stat());

            }catch (Exception e){

            }

            if(bytelock == null){

                oldContext.getLockList().add(lock);

                newlocks.add(lock);

                ops.add(Op.create(pathHelper.lockerPath(lock.getKey()),transaction.getUnique().getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));

                logger.info("Lock:"+lock.getKey());

            }else {

                if(new String(bytelock).equals(transaction.getUnique())){

                    logger.info("ReentrantLock:" + lock.getKey());

                }else{

                    throw  new ConnectException("Can't get the lock");

                }
            }

        }

        ops.add(Op.setData(pathHelper.callPath(transaction,applicationName,call),convert.objectToByteArray(oldContext),AnyVersion));

        try {

            getConnection().multi(ops);

        }catch (Exception e){

            throwException(e);

        }

        return  newlocks;
    }

    @Override
    public synchronized void registerCall(DogTcc transaction, DogCall call, TccContext context) throws ConnectException,InterruptedException ,NonexistException {

        ZkHelp.checkContent(getConnection(),pathHelper.subApplicationPath(transaction,applicationName),true,null);

        List<Op> ops = new ArrayList<Op>();

        Set<TccLock> newLocks = new HashSet<>();

        for(TccLock lock : context.getLockList()){

            byte[] tcc = null;

            try {

                tcc =  getConnection().getData(pathHelper.lockerPath(lock.getKey()),false,new Stat());

            }catch (Exception e){

            }

            if(tcc != null){

                if(new String(tcc).equals(transaction.getUnique())){

                    logger.info("ReentrantLock:" + lock.getKey());

                }else{

                    throw  new ConnectException("锁已被占用");

                }

            }else{

                ops.add(Op.create(pathHelper.lockerPath(lock.getKey()),transaction.getUnique().getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));

                newLocks.add(lock);

                logger.info("Lock:" + lock.getKey());
            }

        }

        context.setLockList(newLocks);

        ops.add(Op.create(pathHelper.callPath(transaction,applicationName,call),convert.objectToByteArray(context),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));

        ops.add(Op.create(pathHelper.callMonitorPath(transaction,applicationName,call),null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL));

        try {

            getConnection().multi(ops);

            logger.info(transaction +" " +"Start Call:" + call +" OK");

        }catch (Exception e){

            logger.error(transaction +" " +"Start Call:" + call +" error");

            throw  new ConnectException();
        }




    }

    @Override
    public synchronized void confirmCall(DogTcc transaction, DogCall call,TccContext context) throws ConnectException, InterruptedException {


        try {

            List<Op> ops = new ArrayList<Op>();

            ops.add(Op.delete(pathHelper.callMonitorPath(transaction,applicationName,call),AnyVersion));

            ops.add(Op.delete(pathHelper.callPath(transaction,applicationName,call),AnyVersion));

            for(TccLock lock : context.getLockList()){

                logger.info("unLock:" + lock.getKey());

                ops.add(Op.delete(pathHelper.lockerPath(lock.getKey()),AnyVersion));

            }

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
