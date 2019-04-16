package top.dogtcc.message.zookeeper;

import org.apache.zookeeper.Op;
import org.apache.zookeeper.data.Stat;
import top.dogtcc.core.entry.*;
import top.dogtcc.core.jms.exception.*;

import top.dogtcc.core.jms.IBroker;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import top.dogtcc.core.common.IBytePackConvert;
import top.dogtcc.core.common.ThreadManager;
import top.dogtcc.message.zookeeper.util.ZkHelp;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static top.dogtcc.message.zookeeper.util.ZkHelp.exist;



public abstract class SimultaneousMessage extends ConnectableMessage implements IBroker {

    @Autowired
    IBytePackConvert convert;

    private static Logger logger = Logger.getLogger(SimultaneousMessage.class);


    public SimultaneousMessage(String applicationName, ZookeeperConfig autoConfig) {

        super(applicationName, autoConfig);

    }


    /**
     * 注册TCC事务
     *
     * @param tcc
     * @throws InterruptedException
     */
    @Override
    public synchronized void registerTcc(DogTcc tcc) throws TccExsitException, ConnectException, InterruptedException {

        ZkHelp.checkContent(getConnection(), pathHelper.tccNamePath(tcc), true, null);

        List<Op> ops = new ArrayList<Op>();

        ops.add(Op.create(pathHelper.tccKeyPath(tcc), DogTccStatus.TRY.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));

        ops.add(Op.create(pathHelper.tccNodesPath(tcc), "NONE".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));

        ops.add(Op.create(pathHelper.tccMonitorContent(tcc), "NONE".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL));


        try {

            getConnection().multi(ops);

        } catch (KeeperException.NodeExistsException e) {

            throw new TccExsitException();

        } catch (KeeperException f) {

            throw new ConnectException();
        }

        logger.debug("registerTcc:"+tcc);

    }

    @Override
    public synchronized void confirmTry(DogTcc tcc) throws TccNotExsitException, ConnectException, InterruptedException {

        checkIfTransactionStarter(tcc);

        try {

            getConnection().setData(pathHelper.tccKeyPath(tcc), DogTccStatus.CONFIRM.getBytes(), AnyVersion);

        } catch (KeeperException e) {

            if(e instanceof  KeeperException.NoNodeException){

                throw  new TccNotExsitException();
            }

           throw  new ConnectException();

        }
        logger.debug("confirmTry:"+tcc);

    }

    @Override
    public synchronized void cancelTry(DogTcc tcc) throws TccNotExsitException, ConnectException, InterruptedException {

        checkIfTransactionStarter(tcc);

        try {

            getConnection().setData(pathHelper.tccKeyPath(tcc), DogTccStatus.CANCEL.getBytes(), AnyVersion);

        } catch (KeeperException e) {

            if(e instanceof  KeeperException.NoNodeException){

                throw  new TccNotExsitException();
            }

            throw  new ConnectException();
        }

        logger.debug("cancelTry:"+tcc);

    }

    @Override
    public synchronized void setContext(DogTcc tcc, DogCall call, TccContext context) throws TccNotExsitException, CallNotExsitException, ConnectException, InterruptedException {

        ZkHelp.checkContent(getConnection(), pathHelper.subApplicationPath(tcc, applicationName), false, null);

        List<Op> ops = new ArrayList<Op>();

        ops.add(Op.setData(pathHelper.callPath(tcc, applicationName, call), convert.objectToByteArray(context), AnyVersion));

        try {

            getConnection().multi(ops);

        } catch (KeeperException e) {

            if(!exist(getConnection(),pathHelper.tccKeyPath(tcc))){

                throw  new TccNotExsitException();
            }

            if(!exist(getConnection(),pathHelper.callPath(tcc, applicationName, call))){

                throw  new CallNotExsitException();
            }

           throw  new ConnectException();

        }

        logger.debug("setContext:"+tcc +" call:"+call);
    }

    @Override
    public synchronized Set<TccLock> lock(DogTcc tcc, DogCall call, Set<TccLock> locks) throws LockExsitException,ConnectException, InterruptedException, TccNotExsitException, CallNotExsitException {

        Set<TccLock> newlocks = new HashSet<>();

        TccContext oldContext = ThreadManager.getContext();

        List<Op> ops = new ArrayList<Op>();

        for (TccLock lock : locks) {

            if (oldContext.getLockList().contains(lock)) {

                continue;

            }

            byte[] bytelock = null;

            try {

                bytelock = getConnection().getData(pathHelper.lockerPath(lock.getKey()), false, new Stat());

            } catch (KeeperException e) {

                if(! (e instanceof KeeperException.NoNodeException)){

                    throw  new ConnectException();

                }

            }

            if (bytelock == null) {

                oldContext.getLockList().add(lock);

                newlocks.add(lock);

                ops.add(Op.create(pathHelper.lockerPath(lock.getKey()), tcc.getUnique().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));

                logger.debug("lock:"+tcc +" lock:"+lock.getKey());

            } else {

                if (new String(bytelock).equals(tcc.getUnique())) {

                    logger.debug("relock:"+tcc +" lock:"+lock.getKey());

                } else {

                    throw new LockExsitException(lock.getKey());

                }
            }


        }

        ops.add(Op.setData(pathHelper.callPath(tcc, applicationName, call), convert.objectToByteArray(oldContext), AnyVersion));

        try {

            getConnection().multi(ops);

        } catch (KeeperException e) {

            if(!exist(getConnection(),pathHelper.tccKeyPath(tcc))){

                throw  new TccNotExsitException();
            }

            if(!exist(getConnection(),pathHelper.callPath(tcc, applicationName, call))){

                throw  new CallNotExsitException();
            }

            if(e instanceof  KeeperException.NodeExistsException){

                throw  new LockExsitException();
            }

            throw  new ConnectException();

        }

        return newlocks;
    }

    @Override
    public synchronized void registerCall(DogTcc transaction, DogCall call, TccContext context) throws LockExsitException,TccNotExsitException, CallExsitException, ConnectException, InterruptedException {

        ZkHelp.checkContent(getConnection(), pathHelper.subApplicationPath(transaction, applicationName), true, null);

        List<Op> ops = new ArrayList<Op>();

        Set<TccLock> newLocks = new HashSet<>();

        for (TccLock lock : context.getLockList()) {

            byte[] tcc = null;

            try {

                tcc = getConnection().getData(pathHelper.lockerPath(lock.getKey()), false, new Stat());

            } catch (KeeperException e) {

                throw  new ConnectException();

            }

            if (tcc != null) {

                if (new String(tcc).equals(transaction.getUnique())) {



                } else {

                    throw new LockExsitException(lock.getKey());

                }

            } else {

                ops.add(Op.create(pathHelper.lockerPath(lock.getKey()), transaction.getUnique().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));

                newLocks.add(lock);

            }

        }

        context.setLockList(newLocks);

        ops.add(Op.create(pathHelper.callPath(transaction, applicationName, call), convert.objectToByteArray(context), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));

        ops.add(Op.create(pathHelper.callMonitorPath(transaction, applicationName, call), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL));

        try {

            getConnection().multi(ops);


        } catch (KeeperException e) {

            if(!exist(getConnection(),pathHelper.tccKeyPath(transaction))){

                throw  new TccNotExsitException();
            }

            if(exist(getConnection(),pathHelper.callPath(transaction, applicationName, call))){

                throw  new CallExsitException();
            }

            if(e instanceof  KeeperException.NodeExistsException){

                throw  new LockExsitException();
            }

            throw  new ConnectException();
        }


        logger.debug("registerCall:"+transaction +" call:"+call);

    }

    @Override
    public synchronized void confirmCall(DogTcc transaction, DogCall call, TccContext context) throws LockNotExsitException, TccNotExsitException, CallNotExsitException, ConnectException, InterruptedException {


        try {

            List<Op> ops = new ArrayList<Op>();

            ops.add(Op.delete(pathHelper.callMonitorPath(transaction, applicationName, call), AnyVersion));

            ops.add(Op.delete(pathHelper.callPath(transaction, applicationName, call), AnyVersion));

            for (TccLock lock : context.getLockList()) {

                ops.add(Op.delete(pathHelper.lockerPath(lock.getKey()), AnyVersion));

            }

            getConnection().multi(ops);

        } catch (KeeperException e) {

            if(!exist(getConnection(),pathHelper.tccKeyPath(transaction))){

                throw  new TccNotExsitException();
            }

            if(!exist(getConnection(),pathHelper.callPath(transaction, applicationName, call))){

                throw  new CallNotExsitException();
            }

            if(e instanceof  KeeperException.NoNodeException){

                throw  new LockNotExsitException();
            }

            throw  new ConnectException();

        }


        logger.debug("confirmCall:"+transaction +" call:"+call);

    }

    @Override
    public synchronized void clearTcc(DogTcc transaction) throws TccNotExsitException, ConnectException, InterruptedException {

        try {

            List<Op> ops = new ArrayList<Op>();

            ops.add(Op.delete(pathHelper.tccMonitorContent(transaction), AnyVersion));

            ops.add(Op.delete(pathHelper.tccKeyPath(transaction), AnyVersion));

            getConnection().multi(ops);


        } catch (KeeperException e) {

            if( e instanceof KeeperException.NoNodeException){

                throw  new TccNotExsitException();
            }

            throw  new ConnectException();
        }

        logger.debug("clearTcc:"+transaction);

    }

}
