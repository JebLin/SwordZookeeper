package indi.sword.util._02_zkClient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.data.Stat;

public class GetData {
    public static void main(String[] args) {

        ZkClient zkClient = new ZkClient("172.18.1.100:2181",10000,10000,new SerializableSerializer());
        System.out.println("connected ok");

        Stat stat = new Stat();
        User u = zkClient.readData("/jike5",stat);
        System.out.println(u.toString());
        System.out.println(stat);

    }
}
