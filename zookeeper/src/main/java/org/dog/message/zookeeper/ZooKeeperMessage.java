package org.dog.message.zookeeper;

import org.dog.core.ApplicationAutoConfig;
import org.dog.core.entry.DogCall;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.DogTccStatus;
import org.dog.core.event.CallNodeOfflineEvent;
import org.dog.core.event.TccAchievementEvent;
import org.dog.core.event.TccNodeOfflineEvent;
import org.dog.core.event.TccTryAchievementEvent;
import org.dog.core.jms.exception.ConnectException;
import org.dog.core.jms.IBroker;
import org.dog.core.jms.exception.NotStartTransactionException;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.dog.core.listener.CallNodeOfflineListener;
import org.dog.core.listener.TccAchievementListener;
import org.dog.core.listener.TccNodeOfflineListener;
import org.dog.core.listener.TccTryAchievementListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ZooKeeperMessage extends SimultaneousMessage implements IBroker {

    protected ScheduledExecutorService scheduledExecutorService = null;

    private static Logger logger = Logger.getLogger(ZooKeeperMessage.class);

    public ZooKeeperMessage(ApplicationAutoConfig applicationAutoConfig, ZookeeperConfig autoConfig) {

        super(applicationAutoConfig.getApplicationname(), autoConfig);

    }

    @Override
    public void watchTccTryAchievement(DogTcc transaction, TccTryAchievementListener listener) throws ConnectException, InterruptedException {


        try {

            byte[] status = zooKeeper.getData(pathHelper.tccKeyPath(transaction), new Watcher() {

                @Override
                public void process(WatchedEvent watchedEvent) {

                    if (watchedEvent.getType().equals(Event.EventType.NodeDataChanged)) {

                        try {

                            byte[] data = zooKeeper.getData(watchedEvent.getPath(), false, new Stat());

                            transaction.setStatus(DogTccStatus.getInstance(data));

                            listener.onTccEvent(new TccTryAchievementEvent(transaction));

                        } catch (InterruptedException | KeeperException e) {

                            logger.error(e);
                        }

                    }

                }
            }, new Stat());


            /**
             * 监控之前数据已经发生了变化  [如节点丢失的情况]
             */
            if (DogTccStatus.getInstance(status).equals(DogTccStatus.CONFIRM) || DogTccStatus.getInstance(status).equals(DogTccStatus.CANCEL)) {

                transaction.setStatus(DogTccStatus.getInstance(status));

                listener.onTccEvent(new TccTryAchievementEvent(transaction));

            }

        } catch (Exception e) {

            logger.error(e);

            throwException(e);

        }
    }

    @Override
    public void watchOffline(TccNodeOfflineListener tccNodeOfflineListener, CallNodeOfflineListener callNodeOfflineListener) throws ConnectException, InterruptedException {

        if (scheduledExecutorService == null) {

            scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

            scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {

                    try {

                        List<String> tccNames = zooKeeper.getChildren(pathHelper.applicationPath(), false);

                        for (String tccname : tccNames) {

                            String tccnamePath = PathHelper.linkPath(pathHelper.applicationPath(), tccname);

                            logger.info("tcc扫描：" + tccnamePath);

                            List<String> tcckeys = zooKeeper.getChildren(tccnamePath, false);

                            for (String tcckey : tcckeys) {

                                String tcckeyPath = PathHelper.linkPath(tccnamePath, tcckey);

                                byte[] tccStatus = null;

                                Stat monitorNode = null;

                                try {

                                    logger.info("tcc扫描：" + tcckeyPath);

                                    tccStatus = zooKeeper.getData(tcckeyPath, false, new Stat());

                                    monitorNode = zooKeeper.exists(PathHelper.linkPath(tcckeyPath, PathHelper.MONITOR), false);

                                } catch (Exception e) {

                                    logger.error(tcckeyPath + "事务已经被清理");

                                    continue;
                                }

                                if (monitorNode == null) {

                                    try {

                                        zooKeeper.create(PathHelper.linkPath(tcckeyPath, PathHelper.MONITOR), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

                                        logger.info("tcc托管：" + tcckeyPath);

                                    } catch (Exception e) {

                                        logger.error(PathHelper.linkPath(tcckeyPath, PathHelper.MONITOR) + "已经被其他线程托管，或者事务已经被清理");

                                        continue;
                                    }

                                    DogTcc dogTcc = new DogTcc(applicationName, tccname, tcckey);

                                    dogTcc.setStatus(DogTccStatus.getInstance(tccStatus));

                                    logger.info("发送tcc 节点掉线事件：" + dogTcc);

                                    tccNodeOfflineListener.onTccEvent(new TccNodeOfflineEvent(dogTcc));
                                }


                            }

                        }

                    } catch (Exception e) {

                        logger.error(e);

                        return;

                    }

                    try {

                        List<String> applications = zooKeeper.getChildren(pathHelper.zookeeperWorkPath(), false);

                        for (String e : applications) {

                            String applicationPath = PathHelper.linkPath(pathHelper.zookeeperWorkPath(), e);

                            List<String> tranNames = zooKeeper.getChildren(applicationPath, false);

                            for (String name : tranNames) {

                                String tranNamePath = PathHelper.linkPath(applicationPath, name);

                                logger.info("call扫描：" + tranNamePath);

                                List<String> trankeys = null;

                                try {

                                    trankeys = zooKeeper.getChildren(tranNamePath, false);

                                } catch (Exception z) {

                                    logger.error(z);

                                    return;
                                }

                                for (String key : trankeys) {

                                    String trankeyPath = PathHelper.linkPath(tranNamePath, key);

                                    watchCallOffline(e, name, key, trankeyPath, callNodeOfflineListener);

                                }

                            }

                        }


                    } catch (Exception e) {

                        logger.info(e);

                    }


                    logger.info("恢复线程结束");

                }
            }, zoolkeepconfig.getInitialdeplay(), zoolkeepconfig.getRecoveryperiod(), TimeUnit.SECONDS);

        }

    }

    private void watchCallOffline(String application, String tccname, String tcckey, String tcckeyPath, CallNodeOfflineListener callNodeOfflineListener) throws Exception {

        String nodesPath = PathHelper.linkPath(tcckeyPath, PathHelper.NODES);

        List<String> subApplicationNames = null;

        try {

            logger.info("call扫描：" + nodesPath);

            subApplicationNames = zooKeeper.getChildren(nodesPath, false);

        } catch (Exception e) {

            logger.info(nodesPath + "已经被清理");

            return;
        }

        for (String subApplicationName : subApplicationNames) {

            if (subApplicationName.equals(applicationName)) {

                String subApplicationPath = PathHelper.linkPath(nodesPath, applicationName);

                List<String> callNames = null;

                try {

                    logger.info("call扫描：" + subApplicationPath);

                    callNames = zooKeeper.getChildren(subApplicationPath, false);

                } catch (Exception e) {

                    logger.info(subApplicationPath + "已经被清理");

                    return;
                }

                List<Pair<DogCall, byte[]>> dogCalls = new ArrayList<>();

                for (String callName : callNames) {

                    String callPath = PathHelper.linkPath(subApplicationPath, callName);

                    Stat stat = null;

                    try {

                        logger.info("call扫描：" + callPath);

                        stat = zooKeeper.exists(PathHelper.linkPath(callPath, PathHelper.MONITOR), false);

                    } catch (Exception e) {

                        logger.info(callPath + "已经被清理");

                        continue;
                    }

                    if (stat == null) {

                        try {

                            zooKeeper.create(PathHelper.linkPath(callPath, PathHelper.MONITOR), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

                        } catch (Exception e) {

                            logger.error(PathHelper.linkPath(callPath, PathHelper.MONITOR) + "已经被其他线程托管,或者事务已经被清理");

                            continue;
                        }


                        DogCall call = new DogCall(callName);

                        byte[] data = zooKeeper.getData(callPath, false, new Stat());

                        dogCalls.add(new Pair<>(call, data));

                    }


                }

                if (dogCalls.size() != 0) {

                    DogTcc dogTcc = new DogTcc(application, tccname, tcckey);

                    dogTcc.setStatus(DogTccStatus.UNKNOWN);

                    CallNodeOfflineEvent calloff = new CallNodeOfflineEvent(dogTcc, dogCalls);

                    logger.info("发送call 节点掉线事件：" + dogTcc);

                    callNodeOfflineListener.onCallEvent(calloff);
                }

            }
        }

    }


    @Override
    public void watchCallsConfirm(DogTcc transaction, TccAchievementListener listener) throws ConnectException, InterruptedException, NotStartTransactionException {

        watchCallsConfirm(transaction, listener, false);

    }


    private void watchCallsConfirm(DogTcc transaction, TccAchievementListener listener, boolean reentry) throws ConnectException, InterruptedException, NotStartTransactionException {


        String path = pathHelper.tccNodesPath(transaction);

        List<String> subApplications = null;


        try {

            subApplications = zooKeeper.getChildren(path, new Watcher() {

                @Override
                public void process(WatchedEvent watchedEvent) {

                    logger.info(path + "发生事件" + watchedEvent);

                    try {

                        if (watchedEvent.getType().equals(Event.EventType.NodeChildrenChanged)) {

                            logger.info(path + "子目录被删除");

                            watchCallsConfirm(transaction, listener, true);

                        } else if (watchedEvent.getType().equals(Event.EventType.NodeDeleted)) {

                            logger.info(path + " 目录已删除");

                        }

                    } catch (Exception e) {

                        logger.info(e);

                    }
                }
            });


            if (subApplications.isEmpty()) {

                logger.info(path + "子目录为空");

                try {

                    zooKeeper.delete(path, AnyVersion);

                } catch (Exception e) {

                    logger.error(e);
                }

                logger.info(path + "回调事件：" + transaction);

                listener.onTccEvent(new TccAchievementEvent(transaction));

            } else {

                if (!reentry) {

                    for (String subApplication : subApplications) {

                        watchSubApplicationConfirm(PathHelper.linkPath(path, subApplication));

                    }

                }
            }

        } catch (Exception e) {

            logger.error(e);

            listener.onTccEvent(new TccAchievementEvent(transaction));
        }


    }


    private void watchSubApplicationConfirm(String subApplication) throws ConnectException, InterruptedException {


        logger.info("查询目录:" + subApplication);


        try {

            List<String> calls = zooKeeper.getChildren(subApplication, new Watcher() {

                @Override
                public void process(WatchedEvent watchedEvent) {

                    logger.info(subApplication + "发生事件" + watchedEvent);

                    /**
                     * 先发生NodeChildrenChanged 再发生 NodeDeleted
                     */
                    if (watchedEvent.getType().equals(Event.EventType.NodeChildrenChanged)) {

                        logger.info(subApplication + ":子目录被删除");

                        try {

                            watchSubApplicationConfirm(subApplication);

                        } catch (Exception e) {

                            logger.error(e);

                        }

                    } else if (watchedEvent.getType().equals(Event.EventType.NodeDeleted)) {

                        logger.info(subApplication + ":目录已删除");
                    }

                }
            });


            logger.info("目录:" + subApplication + ":子目录数：" + calls.size());


            if (calls.isEmpty()) {

                logger.info("目录：" + subApplication + "为空");

                zooKeeper.delete(subApplication, AnyVersion);

                logger.info("删除目录：" + subApplication);

            }

        } catch (KeeperException | InterruptedException e) {

            throwException(e);
        }

    }


    public void close() throws IOException {


        scheduledExecutorService.shutdown();

        super.close();

    }

}
