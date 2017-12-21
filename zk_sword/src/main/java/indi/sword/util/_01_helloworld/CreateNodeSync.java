package indi.sword.util._01_helloworld;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * @Decription 同步创建节点
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/21 15:15
 */
public class CreateNodeSync implements Watcher {

    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws InterruptedException, IOException {
        zooKeeper = new ZooKeeper("172.18.1.100:2181",5000,new CreateNodeSync());
        System.out.println(zooKeeper.getState());
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("收到事件：" + watchedEvent );
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
            if(watchedEvent.getType() == Event.EventType.None && null == watchedEvent.getPath()){
                doSomething();
            }
        }
    }

    private void doSomething() {
        try {
            String path = zooKeeper.create("/node_4","123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("return path:" + path);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("do something ...");
    }
}
