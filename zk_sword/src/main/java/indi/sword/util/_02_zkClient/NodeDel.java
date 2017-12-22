package indi.sword.util._02_zkClient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

public class NodeDel {
    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("172.18.1.100",10000,10000,new SerializableSerializer());
        System.out.println("Connected ok!");
        boolean e = zkClient.exists("/jike20");
        if(e){
            System.out.println("------- enter del ------- ");
            System.out.println(zkClient.delete("/jike20"));
        }
    }
}
