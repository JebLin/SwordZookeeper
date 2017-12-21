package indi.sword.util._01_helloworld;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * @Decription 异步得到子节点数据
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/21 16:27
 */
public class GetChildrenASync implements Watcher{

    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws IOException, InterruptedException {
        zooKeeper = new ZooKeeper("172.18.1.100:2181",5000,new GetChildrenASync());
        System.out.println(zooKeeper.getState().toString());
        System.out.println("-----------------------------");
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("收到事件：" + watchedEvent );
        if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
            if(watchedEvent.getType()== Event.EventType.None && null==watchedEvent.getPath()){
                doSomething(zooKeeper);
            }else{
                if(watchedEvent.getType()== Event.EventType.NodeChildrenChanged){
                    zooKeeper.getChildren(watchedEvent.getPath(),true,new IChildren2Callback(),null);
                }
            }
        }
    }

    private void doSomething(ZooKeeper zooKeeper) {
        try {
            System.out.println("doSomething ... ");
            zooKeeper.getChildren("/",true,new IChildren2Callback(),null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class IChildren2Callback implements AsyncCallback.Children2Callback{
        @Override
        public void processResult(int rc, String path, Object ctx,
                                  List<String> children, Stat stat) {

            StringBuilder sb = new StringBuilder();
            sb.append("rc="+rc).append("\n");
            sb.append("path="+path).append("\n");
            sb.append("ctx="+ctx).append("\n");
            sb.append("children="+children).append("\n");
            sb.append("stat="+stat).append("\n");
            System.out.println(sb.toString());
        }


    }

}
