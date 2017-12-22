package indi.sword.util._02_zkClient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

public class WriteData {
    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("172.18.1.100:2181",10000,10000,new SerializableSerializer());
        System.out.println("connected ok !");

        User u = new User();
        u.setId(2);
        u.setName("test2");
        zkClient.writeData("/jike5",u,0); // CAS 写数据 ZkBadVersionException表示版本不对
    }
}
