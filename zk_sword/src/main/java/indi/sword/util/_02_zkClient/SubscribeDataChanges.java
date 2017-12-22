package indi.sword.util._02_zkClient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;

public class SubscribeDataChanges {
    public static void main(String[] args) throws InterruptedException {
        ZkClient zkClient = new ZkClient("172.18.1.100:2181",10000,10000,new BytesPushThroughSerializer());
        System.out.println("connected ok !");

        zkClient.subscribeDataChanges("/jike20",new ZkDataListener());
        Thread.sleep(Integer.MAX_VALUE);
    }

    private static class ZkDataListener implements IZkDataListener{

        @Override
        public void handleDataChange(String dataPath, Object data) throws Exception {
            System.out.println("handleDataChange -> " + dataPath + ":" + data);
        }

        @Override
        public void handleDataDeleted(String dataPath) throws Exception {
            System.out.println("handleDataDeleted -> " + dataPath);
        }
    }
}
