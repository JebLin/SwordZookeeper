package indi.sword.util._01_helloworld;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

public class UpdateNodeASync implements Watcher{
    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws InterruptedException, IOException {
        zooKeeper = new ZooKeeper("172.18.1.100:2181",5000,new UpdateNodeASync());
        System.out.println(zooKeeper.getState());
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("得到事件 ：" + watchedEvent);
        if(watchedEvent.getState()== Event.KeeperState.SyncConnected){
            if(watchedEvent.getType() == Event.EventType.None && null == watchedEvent.getPath()){
                doSomething();
            }
        }
    }

    private void doSomething() {
        zooKeeper.setData("/node_6","777".getBytes(),-1,new IStatCallback(),null);
    }

    static class IStatCallback implements AsyncCallback.StatCallback{

        @Override
        public void processResult(int rc, String path, Object ctx, Stat stat) {
            StringBuilder sb = new StringBuilder();
            sb.append("rc="+rc).append("\n");
            sb.append("path"+path).append("\n");
            sb.append("ctx="+ctx).append("\n");
            sb.append("Stat="+stat).append("\n");
            System.out.println(sb.toString());

        }
    }
}
