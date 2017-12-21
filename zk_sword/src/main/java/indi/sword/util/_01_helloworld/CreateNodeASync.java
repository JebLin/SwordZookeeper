package indi.sword.util._01_helloworld;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * @Decription 异步创建节点
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/21 15:21
 */
public class CreateNodeASync implements Watcher{

    private static ZooKeeper zooKeeper;
    public static void main(String[] args) throws InterruptedException, IOException {
        zooKeeper = new ZooKeeper("172.18.1.100:2181",5000,new CreateNodeASync());
        System.out.println(zooKeeper.getState());
        System.out.println("--------------------");
        Thread.sleep(Integer.MAX_VALUE);
    }


    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("收到事件：" + watchedEvent);
        if(watchedEvent.getState() == Event.KeeperState.SyncConnected){  // 监听连接时间，连接完zookeeper 干啥事
            if(watchedEvent.getType() == Event.EventType.None && null == watchedEvent.getPath()){ // Event.EventType.None 这个节点没啥事
                doSomething();
            }
        }
    }

    private void doSomething() {
        zooKeeper.create("/node_5","123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT,new IStringCallback(),"创建");
    }

    static class IStringCallback implements AsyncCallback.StringCallback{

        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            StringBuilder sb = new StringBuilder();
            sb.append("rc = " + rc).append("\n");
            sb.append("path = " + path).append("\n");
            sb.append("ctx = " + ctx).append("\n");
            sb.append("name = " + name).append("\n");
            System.out.println(sb.toString());
        }
    }
}


