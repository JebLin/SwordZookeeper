package indi.sword.util._01_helloworld;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import java.io.IOException;
/**
 * @Decription 测试连接
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/21 14:48
 */
public class CreateSession implements Watcher{

    private static ZooKeeper zookeeper;

    public static void main(String[] args) throws IOException, InterruptedException {

        zookeeper = new ZooKeeper("172.18.1.100:2181",5000,new CreateSession());
        System.out.println(zookeeper.getState());
        Thread.sleep(Integer.MAX_VALUE);
    }

    public void doSomething(){
        System.out.println("do something ...");
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("收到事件：  " + watchedEvent);

        if(watchedEvent.getState() == KeeperState.SyncConnected){
            if(watchedEvent.getType() == EventType.None && null == watchedEvent.getPath()){
                doSomething();
            }
        }
    }
}
