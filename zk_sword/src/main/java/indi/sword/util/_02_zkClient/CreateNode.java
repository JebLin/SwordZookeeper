package indi.sword.util._02_zkClient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.CreateMode;

public class CreateNode {
    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("172.18.1.100:2181,172.18.1.103:2181,172.18.2.112:2181",10000,10000,new SerializableSerializer());
        System.out.println("connected ok!");

        User u = new User(); // user 要实现序列化接口
        u.setId(1);
        u.setName("test");
        String path = zkClient.create("/jike20/jike22222",u, CreateMode.PERSISTENT);
        System.out.println("create path : " + path);
    }
}
