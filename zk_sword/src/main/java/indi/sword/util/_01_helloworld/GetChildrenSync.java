package indi.sword.util._01_helloworld;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;

/**
 * @Decription 同步得到子节点
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/21 15:42
 */
public class GetChildrenSync implements Watcher{

    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws InterruptedException, IOException {
        zooKeeper = new ZooKeeper("172.18.1.100:2181",5000,new GetChildrenSync());
        System.out.println(zooKeeper.getState().toString());
        System.out.println("-----------------------------");
        Thread.sleep(Integer.MAX_VALUE);
    }
    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("收到事件：" + watchedEvent );
        if(watchedEvent.getState()== Event.KeeperState.SyncConnected){
            if(watchedEvent.getType()== Event.EventType.None && null==watchedEvent.getPath()){
                doSomething();
            }
        }
    }

    private void doSomething() {
        List<String> children = null;
        try {
            children = zooKeeper.getChildren("/",true);
            System.out.println(children);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
