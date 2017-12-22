package indi.sword.util._02_zkClient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

public class NodeExists {
    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("172.18.1.100:2181",10000,10000,new SerializableSerializer());
        System.out.println("connected ok");
        boolean e = zkClient.exists("/jike55");
        System.out.println(e);

    }
}
