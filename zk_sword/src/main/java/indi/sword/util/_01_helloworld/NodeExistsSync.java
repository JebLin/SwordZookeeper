package indi.sword.util._01_helloworld;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

public class NodeExistsSync implements Watcher {

    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws InterruptedException, IOException {
        zooKeeper = new ZooKeeper("172.18.1.100:2181",5000,new NodeExistsSync());
        System.out.println(zooKeeper.getState());
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("收到事件：" + watchedEvent );
        if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
            if(watchedEvent.getType() == Event.EventType.None && null == watchedEvent.getPath()){
                doSomething(zooKeeper);
            }else{
                try {
                    if(watchedEvent.getType() == Event.EventType.NodeCreated){
                        System.out.println(watchedEvent.getPath() + " create");
                        System.out.println(zooKeeper.exists(watchedEvent.getPath(),true));
                    }else if(watchedEvent.getType() == Event.EventType.NodeDataChanged){
                        System.out.println(watchedEvent.getPath()+" update");
                        System.out.println(zooKeeper.exists(watchedEvent.getPath(),true));
                    }else if(watchedEvent.getType()== Event.EventType.NodeDeleted){
                        System.out.println(watchedEvent.getPath() + " delete");
                        System.out.println(zooKeeper.exists(watchedEvent.getPath(),true));
                    }
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doSomething(ZooKeeper zooKeeper) {
        Stat stat = null;
        try {
            stat = zooKeeper.exists("/node_1",true);
            System.out.println("doSomething -> stat == " + stat);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
