package indi.sword.util._01_helloworld;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * @Decription 修改节点
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/22 14:57
 */
public class UpdateNodeSync implements Watcher{

    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws InterruptedException, IOException {
        zooKeeper = new ZooKeeper("172.18.1.100:2181",5000,new UpdateNodeSync());
        System.out.println(zooKeeper.getState());
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("得到事件：" + watchedEvent);
        if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
            if(watchedEvent.getType() == Event.EventType.None && null == watchedEvent.getPath() ){
                doSomething();
            }
        }
    }

    private void doSomething() {
        Stat stat = null;
        try {
            stat = zooKeeper.setData("/node_6","666".getBytes(),-1);
            System.out.println(stat);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
