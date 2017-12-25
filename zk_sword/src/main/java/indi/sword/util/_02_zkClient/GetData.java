package indi.sword.util._02_zkClient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.data.Stat;

public class GetData {
    public static void main(String[] args) {

        String server112 = "172.18.2.112:2181";
        String server100 = "172.18.1.100:2181";
        String server103 = "172.18.1.103:2181";
        String clusterServer = server112.concat(",").concat(server100).concat(",").concat(server103);
        ZkClient zkClient = new ZkClient("172.18.2.112:2181,172.18.1.100:2181,172.18.1.103:2181",10000,10000,new SerializableSerializer());
        System.out.println("connected ok");

        Stat stat = new Stat();
        User u = zkClient.readData("/jike5",stat);
        System.out.println(u.toString());
        System.out.println(stat);

    }
}
