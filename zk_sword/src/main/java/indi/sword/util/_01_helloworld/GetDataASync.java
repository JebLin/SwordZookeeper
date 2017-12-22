package indi.sword.util._01_helloworld;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * @Decription 异步获取数据
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/22 11:50
 */
public class GetDataASync implements Watcher{

    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws InterruptedException, IOException {
        zooKeeper = new ZooKeeper("172.18.1.100:2181",5000,new GetDataASync());
        System.out.println(zooKeeper.getState());
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getState()== Event.KeeperState.SyncConnected){
            if(watchedEvent.getType() == Event.EventType.None && null == watchedEvent.getPath()){
                doSomething();
            }else{
                if(watchedEvent.getType() == Event.EventType.NodeDataChanged){ // 节点数据变化
                    try {
                        zooKeeper.getData(watchedEvent.getPath(),true,new IDataCallback(),null);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void doSomething() {
        zooKeeper.getData("/node_1",true,new IDataCallback(),null);
    }

    static class IDataCallback implements AsyncCallback.DataCallback{

        @Override
        public void processResult(int rc, String path, Object ctx, byte[] bytes, Stat stat) {
            try {
                System.out.println(new String(zooKeeper.getData(path,true,stat)));
                System.out.println(stat);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
