package top.dogtcc.message.zookeeper.watcher;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.DogTccStatus;
import top.dogtcc.core.event.TccTryAchievementEvent;
import top.dogtcc.core.listener.TccTryAchievementListener;

public class TccTryWatcher implements Watcher {

    private static Logger logger = Logger.getLogger(TccTryWatcher.class);

    private ZooKeeper zooKeeper;

    private TccTryAchievementListener listener;

    private DogTcc tcc;

    public TccTryWatcher(DogTcc tcc,TccTryAchievementListener listener, ZooKeeper zooKeeper){

        this.tcc = tcc;

        this.listener = listener;

        this.zooKeeper = zooKeeper;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {


        if (watchedEvent.getType().equals(Event.EventType.NodeDataChanged)) {

            try {

                byte[] data = zooKeeper.getData(watchedEvent.getPath(), false, new Stat());

                tcc.setStatus(DogTccStatus.getInstance(data));

                listener.onTccEvent(new TccTryAchievementEvent(tcc));

            } catch (InterruptedException | KeeperException e) {

                logger.error(e);
            }

        }

    }
}
