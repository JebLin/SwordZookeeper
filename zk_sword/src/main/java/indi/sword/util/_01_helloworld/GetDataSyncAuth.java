package indi.sword.util._01_helloworld;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * @Decription 授权才能 getData
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/22 16:24
 */
public class GetDataSyncAuth implements Watcher{

    private static ZooKeeper zookeeper;
    private static Stat stat = new Stat();

    public static void main(String[] args) throws IOException, InterruptedException {
        zookeeper = new ZooKeeper("172.18.1.100:2181",5000,new GetDataSyncAuth());
        System.out.println(zookeeper.getState().toString());

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("得到事件：" + watchedEvent);
        if (watchedEvent.getType()== Event.EventType.None && null==watchedEvent.getPath()){
            doSomething();
        }else{
            if (watchedEvent.getType()== Event.EventType.NodeDataChanged){
                try {
                    System.out.println(new String(zookeeper.getData(watchedEvent.getPath(), true, stat)));
                    System.out.println("stat:"+stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doSomething() {
        zookeeper.addAuthInfo("digest","jike:123456".getBytes()); // 这里加个密码上去
        try {
            System.out.println(new String(zookeeper.getData("/node_666",true,stat)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
