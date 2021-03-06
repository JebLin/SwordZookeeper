package indi.sword.util._03_curator_cruda;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryUntilElapsed;

public class NodeChildrenListener {
    public static void main(String[] args) throws Exception {
        //RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        //RetryPolicy retryPolicy = new RetryNTimes(5, 1000);
        RetryPolicy retryPolicy = new RetryUntilElapsed(5000, 1000);
//		CuratorFramework client = CuratorFrameworkFactory
//				.newClient("192.168.1.105:2181",5000,5000, retryPolicy);

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("172.18.1.100:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();

        client.start();

        final PathChildrenCache cache = new PathChildrenCache(client,"/jike",true);
        cache.start();
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                switch (pathChildrenCacheEvent.getType()){
                    case CHILD_ADDED:;
                        System.out.println("CHILD_ADDED: " + pathChildrenCacheEvent.getData());break;
                    case CHILD_UPDATED:;
                        System.out.println("CHILD_UPDATED: " + pathChildrenCacheEvent.getData());break;
                    case CHILD_REMOVED:;
                        System.out.println("CHILD_REMOVED: " + pathChildrenCacheEvent.getData());break;
                }
            }
        });

        Thread.sleep(Integer.MAX_VALUE);
    }
}
