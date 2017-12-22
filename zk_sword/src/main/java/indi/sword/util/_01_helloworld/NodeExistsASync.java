package indi.sword.util._01_helloworld;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * @Decription 节点是否存在
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/22 14:39
 */
public class NodeExistsASync implements Watcher{

    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws InterruptedException, IOException {
        zooKeeper = new ZooKeeper("172.18.1.100:2181",5000,new NodeExistsASync());
        System.out.println(zooKeeper.getState());
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("收到事件：" + watchedEvent );
        if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
            if(watchedEvent.getType() == Event.EventType.None && null == watchedEvent.getPath()){
                doSomething();
            }
        }else{
            try {
                if(watchedEvent.getType() == Event.EventType.NodeCreated){
                    System.out.println(watchedEvent.getPath() + " create");
                    zooKeeper.exists(watchedEvent.getPath(),true,new IStateCallback(),null);
                }else if(watchedEvent.getType() == Event.EventType.NodeDataChanged){
                    System.out.println(watchedEvent.getPath() + " updated");
                    zooKeeper.exists(watchedEvent.getPath(),true,new IStateCallback(),null);
                }else if(watchedEvent.getType() == Event.EventType.NodeDeleted){
                    System.out.println(watchedEvent.getPath() + " deleted");
                    zooKeeper.exists(watchedEvent.getPath(),true,new IStateCallback(),null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class IStateCallback implements AsyncCallback.StatCallback{

        @Override
        public void processResult(int rc, String path, Object ctx, Stat stat) {
            System.out.println("rc : " + rc);
            System.out.println("path : " + path);
            System.out.println("ctx : " + ctx);
            System.out.println("stat : " + stat);
        }
    }

    private void doSomething() {
        zooKeeper.exists("/node_1",true,new IStateCallback(),null);
    }


}
