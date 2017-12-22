package indi.sword.util._01_helloworld;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * @Decription 同步删除操作
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/22 12:26
 */
public class DeleteNodeSync implements Watcher{

    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws IOException, InterruptedException {
        zooKeeper = new ZooKeeper("172.18.1.100:2181",5000,new DeleteNodeSync());
        System.out.println(zooKeeper.getState());
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("收到事件：" + watchedEvent );
        if(watchedEvent.getState()== Event.KeeperState.SyncConnected){ // 反正一连上就干活
            if(watchedEvent.getType() == Event.EventType.None && null == watchedEvent.getPath()){ // 事件没有任何类型，事件也没路径  也就是只是连上了，没发生啥事情嘛
                doSomething();

            }
        }
    }

    private void doSomething() {
        try {
            zooKeeper.delete("/node_20",-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
