package top.dogtcc.message.zookeeper;

import top.dogtcc.core.ApplicationAutoConfig;
import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.DogTccStatus;
import top.dogtcc.core.event.CallNodeOfflineEvent;
import top.dogtcc.core.event.TccAchievementEvent;
import top.dogtcc.core.event.TccNodeOfflineEvent;
import top.dogtcc.core.event.TccTryAchievementEvent;
import top.dogtcc.core.jms.ICallNode;
import top.dogtcc.core.jms.ITccNode;
import top.dogtcc.core.jms.exception.ConnectException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import top.dogtcc.core.listener.CallNodeOfflineListener;
import top.dogtcc.core.listener.TccAchievementListener;
import top.dogtcc.core.listener.TccNodeOfflineListener;
import top.dogtcc.core.listener.TccTryAchievementListener;
import top.dogtcc.core.common.Pair;
import top.dogtcc.message.zookeeper.util.PathHelper;
import top.dogtcc.message.zookeeper.watcher.TccTryWatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static top.dogtcc.message.zookeeper.util.ZkHelp.throwException;

@Component
public class ZooKeeperMessage extends SimultaneousMessage implements ICallNode, ITccNode {

    protected ScheduledExecutorService scheduledExecutorService  = new ScheduledThreadPoolExecutor(2);;

    private static Logger logger = Logger.getLogger(ZooKeeperMessage.class);

    public ZooKeeperMessage(ApplicationAutoConfig applicationAutoConfig, ZookeeperConfig autoConfig) {

        super(applicationAutoConfig.getApplicationname(), autoConfig);

    }

    @Override
    public void addTryAchievementListener(DogTcc transaction, TccTryAchievementListener listener) throws ConnectException, InterruptedException {


        try {

            byte[] status = getConnection().getData(pathHelper.tccKeyPath(transaction), new TccTryWatcher(
                    transaction, listener, getConnection()), new Stat());

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
    public void addCallOfflineListener(CallNodeOfflineListener callNodeOfflineListener) throws ConnectException, InterruptedException {


            scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {

                    try {

                        List<String> applications = getConnection().getChildren(pathHelper.zookeeperWorkPath(), false);

                        for (String tccStartapplication : applications) {

                            /**
                             * /root/application1
                             */
                            String applicationPath = PathHelper.linkPath(pathHelper.zookeeperWorkPath(), tccStartapplication);

                            List<String> tccNames = getConnection().getChildren(applicationPath, false);

                            for (String tccpath : tccNames) {

                                /**
                                 * /root/application1/tranname
                                 */
                                String tccNamePath = PathHelper.linkPath(applicationPath, tccpath);

                                List<String> trankeys = getConnection().getChildren(tccNamePath, false);

                                for (String tcckey : trankeys) {

                                    /**
                                     * /root/application1/tranname/trankey
                                     */
                                    String tcckeyPath = PathHelper.linkPath(tccNamePath, tcckey);

                                    watchCallOffline(tccStartapplication, tccpath, tcckey, tcckeyPath, callNodeOfflineListener);

                                }

                            }

                        }

                    } catch (Exception e) {

                        logger.error(e);

                        return;

                    }

                    logger.info("恢复线程结束");

                }
            }, zoolkeepconfig.getInitialdeplay(), zoolkeepconfig.getRecoveryperiod(), TimeUnit.SECONDS);



    }

    @Override
    public void addTccOfflineListner(TccNodeOfflineListener tccNodeOfflineListener) throws ConnectException, InterruptedException {

            scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {

                    try {

                        watchTccOffline(tccNodeOfflineListener);

                    } catch (Exception e) {

                        logger.error(e);

                        return;

                    }

                    logger.info("恢复线程结束");

                }
            }, zoolkeepconfig.getInitialdeplay(), zoolkeepconfig.getRecoveryperiod(), TimeUnit.SECONDS);

    }


    private void watchTccOffline(TccNodeOfflineListener tccNodeOfflineListener) throws ConnectException, InterruptedException {

        try {

            List<String> tccNames = getConnection().getChildren(pathHelper.applicationPath(), false);

            for (String tccname : tccNames) {

                String tccnamePath = PathHelper.linkPath(pathHelper.applicationPath(), tccname);

                logger.info("tcc扫描：" + tccnamePath);

                List<String> tcckeys = getConnection().getChildren(tccnamePath, false);

                for (String tcckey : tcckeys) {

                    String tcckeyPath = PathHelper.linkPath(tccnamePath, tcckey);

                    try {

                        getConnection().create(PathHelper.linkPath(tcckeyPath, PathHelper.MONITOR), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

                        byte[] tccStatus = getConnection().getData(tcckeyPath, false, new Stat());

                        DogTcc dogTcc = new DogTcc(applicationName, tccname, tcckey);

                        dogTcc.setStatus(DogTccStatus.getInstance(tccStatus));

                        logger.info("发送tcc 节点掉线事件：" + dogTcc);

                        tccNodeOfflineListener.onTccEvent(new TccNodeOfflineEvent(dogTcc));

                    } catch (Exception e) {

                        continue;

                    }

                }

            }

        } catch (Exception e) {

            throwException(e);
        }

    }


    private void watchCallOffline(String application, String tccname, String tcckey, String tcckeyPath, CallNodeOfflineListener callNodeOfflineListener) throws Exception {


        String nodesPath = PathHelper.linkPath(tcckeyPath, PathHelper.NODES);

        List<String> subApplicationNames = null;

        try {

            subApplicationNames = getConnection().getChildren(nodesPath, false);

        } catch (Exception e) {

            return;
        }

        for (String subApplicationName : subApplicationNames) {

            if (subApplicationName.equals(applicationName)) {

                String subApplicationPath = PathHelper.linkPath(nodesPath, applicationName);

                List<String> calluuids = null;

                try {

                    calluuids = getConnection().getChildren(subApplicationPath, false);

                } catch (Exception e) {

                    continue;
                }

                List<Pair<DogCall, byte[]>> dogCalls = new ArrayList<>();

                for (String uuid : calluuids) {

                    String callPath = PathHelper.linkPath(subApplicationPath, uuid);

                    try {

                        getConnection().create(PathHelper.linkPath(callPath, PathHelper.MONITOR), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

                    } catch (Exception e) {

                        continue;
                    }

                    DogCall call = new DogCall(uuid,applicationName);

                    byte[] data = getConnection().getData(callPath, false, new Stat());

                    dogCalls.add(new Pair<>(call, data));

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
    public void addTccAchievementListener(DogTcc transaction, TccAchievementListener listener) throws ConnectException, InterruptedException{

        watchCallsConfirm(transaction, listener, false);

    }


    private void watchCallsConfirm(DogTcc transaction, TccAchievementListener listener, boolean reentry) throws ConnectException, InterruptedException{


        String path = pathHelper.tccNodesPath(transaction);

        List<String> subApplications = null;


        try {

            subApplications = getConnection().getChildren(path, new Watcher() {

                @Override
                public void process(WatchedEvent watchedEvent) {

                    logger.info(path + "发生事件" + watchedEvent);

                    try {

                        if (watchedEvent.getType().equals(Event.EventType.NodeChildrenChanged)) {

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

                try {

                    getConnection().delete(path, AnyVersion);

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

        try {

            List<String> calls = getConnection().getChildren(subApplication, new Watcher() {

                @Override
                public void process(WatchedEvent watchedEvent) {

                    /**
                     * 先发生NodeChildrenChanged 再发生 NodeDeleted
                     */
                    if (watchedEvent.getType().equals(Event.EventType.NodeChildrenChanged)) {

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


            if (calls.isEmpty()) {

                getConnection().delete(subApplication, AnyVersion);

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
