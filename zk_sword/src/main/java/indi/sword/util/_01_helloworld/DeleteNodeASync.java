package indi.sword.util._01_helloworld;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class DeleteNodeASync implements Watcher {


    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws IOException, InterruptedException {
        zooKeeper = new ZooKeeper("172.18.1.100:2181",5000,new DeleteNodeASync());
        System.out.println(zooKeeper.getState());
        Thread.sleep(Integer.MAX_VALUE);
    }
    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("得到事件：" + watchedEvent);
        if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
            if(watchedEvent.getType() == Event.EventType.None && null == watchedEvent.getPath()){
                doSomething();
            }
        }
    }

    private void doSomething() {
        zooKeeper.delete("/node_6",-1,new IVoidCallback(),null);
    }

    static class IVoidCallback implements AsyncCallback.VoidCallback{

        @Override
        public void processResult(int rc, String path, Object ctx) {
            StringBuilder sb = new StringBuilder();
            sb.append("rc = " + rc).append("\n");
            sb.append("path = " + path).append("\n");
            sb.append("ctx = " + ctx).append("\n");
            System.out.println(sb.toString());

        }
    }
}
