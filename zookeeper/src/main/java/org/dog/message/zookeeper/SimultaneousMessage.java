package org.dog.message.zookeeper;

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

        /**
         *  /root/application/transactions
         */
        checkContent(pathHelper.tccNamePath(tcc),true,null);

        /**
         *  /root/application/transactionname/transactionkey
         */
        try{

            zooKeeper.create(pathHelper.tccKeyPath(tcc), DogTccStatus.TRY.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        }catch (KeeperException|InterruptedException e){

            logger.error(e);

            throwException(e);
        }

        logger.info("创建："+ pathHelper.tccKeyPath(tcc));


        /**
         *  /root/application/transactionname/transactionkey/nodes
         */
        checkContent(pathHelper.tccNodesPath(tcc),true,null);


        /**
         *  /root/application/transactionname/transactionkey/monitor
         */
        try {

            zooKeeper.create(pathHelper.tccMonitorContent(tcc),null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        }catch (KeeperException|InterruptedException e ){

            if( e instanceof  KeeperException){

                logger.info(tcc+"被托管");
                logger.info(e);

            }else {

                logger.error(e);
                throwException(e);

            }

        }

        logger.info("创建："+ pathHelper.tccMonitorContent(tcc));

    }

    @Override
    public synchronized void confirmTry(DogTcc tcc) throws NotStartTransactionException,ConnectException ,InterruptedException{

        checkIfTransactionStarter(tcc);

        /**
         * /root/application/transactionname/transactionkey  - confirm
         */
        try{

            zooKeeper.setData(pathHelper.tccKeyPath(tcc), DogTccStatus.CONFIRM.getBytes(),AnyVersion);

        }catch (KeeperException|InterruptedException e){

            logger.error(e);

            throwException(e);
        }

        logger.info("设置："+ pathHelper.tccKeyPath(tcc) +"  " + DogTccStatus.CONFIRM);

    }

    @Override
    public synchronized void cancelTry(DogTcc tcc) throws NotStartTransactionException,ConnectException ,InterruptedException{

        checkIfTransactionStarter(tcc);

        /**
         * /root/application/transactionname/transactionkey  - cancel
         */

        try {

            zooKeeper.setData(pathHelper.tccKeyPath(tcc), DogTccStatus.CANCEL.getBytes(),AnyVersion);

        }catch (KeeperException|InterruptedException e){

            logger.error(e);

            throwException(e);
        }


        logger.info("设置："+ pathHelper.tccKeyPath(tcc) +"  " + DogTccStatus.CANCEL);


    }

    @Override
    public synchronized void registerCall(DogTcc transaction, DogCall call, byte[] data) throws ConnectException,InterruptedException ,NonexistException {

        checkContent(pathHelper.subApplicationPath(transaction,applicationName),true,null);

        /**
         * /root/application/transactionname/transactionkey/nodes/subapplication/serverName  create
         */
        checkContent(pathHelper.callPath(transaction,applicationName,call),true,data);

        /**
         *   /root/application/transactionname/transactionkey/nodes/subapplication/serverName/minitor create
         */
        try {

            zooKeeper.create(pathHelper.callMonitorPath(transaction,applicationName,call),null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        }catch (KeeperException|InterruptedException e ){

            if( e instanceof  KeeperException){

                logger.info(transaction+"被托管");
                logger.info(e);

            }else {

                logger.error(e);
                throwException(e);

            }

        }


    }

    @Override
    public synchronized void confirmCall(DogTcc transaction, DogCall call) throws ConnectException, InterruptedException {

        try {

            zooKeeper.delete(pathHelper.callMonitorPath(transaction,applicationName,call),AnyVersion);

            zooKeeper.delete(pathHelper.callPath(transaction,applicationName,call),AnyVersion);


            logger.info("删除："+ pathHelper.callPath(transaction,applicationName,call) +"  " + DogCallStatus.CONFIRM);


        }catch (Exception e){

            logger.error("删除时，被其他节点托管！，将由其他节点删除！");

            throwException(e);
        }


    }

    @Override
    public synchronized void clearTcc(DogTcc transaction) throws ConnectException, InterruptedException{

        try {

            /**
             * 级联删除事务关联目录
             */
            ZKUtil.rmr(pathHelper.tccKeyPath(transaction),zooKeeper);

            logger.info("clearTcc:"+ transaction);

        }catch (KeeperException|InterruptedException  e){

            logger.error(e);

            throwException(e);
        }

    }

}
