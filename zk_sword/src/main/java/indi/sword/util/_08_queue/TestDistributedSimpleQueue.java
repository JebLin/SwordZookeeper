package indi.sword.util._08_queue;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

/**
 * @Decription 测试分布式队列，FIFO
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/25 16:43
 */
public class TestDistributedSimpleQueue {
    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("172.18.1.100:2181", 5000, 5000, new SerializableSerializer());
        DistributedSimpleQueue<User> queue = new DistributedSimpleQueue<User>(zkClient, "/Queue");

        try {
            for (int i = 0; i < 10; i++) {
                User user = new User();
                user.setId("id_" + i);
                user.setName("name_" + i);
                queue.offer(user);
                System.out.println(user.toString());
                Thread.sleep((i+1) * 1000 );
            }

            for (int i = 0; i < 10; i++) {
                User u = queue.poll();
                System.out.println(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
