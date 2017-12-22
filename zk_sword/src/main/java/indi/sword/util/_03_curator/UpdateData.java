package indi.sword.util._03_curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.data.Stat;

public class UpdateData {
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new RetryUntilElapsed(5000,1000);

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("172.18.1.100:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();

        client.start();

        Stat stat = new Stat();
        byte[] ret = client.getData().storingStatIn(stat).forPath("/jike");
        System.out.println(new String(ret));
        client.setData().withVersion(stat.getVersion()).forPath("/jike","jike777".getBytes());

        Thread.sleep(Integer.MAX_VALUE);
    }
}
