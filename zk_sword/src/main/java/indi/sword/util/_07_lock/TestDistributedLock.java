package indi.sword.util._07_lock;

import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;

/**
 * @Decription 测试类
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/24 17:32
 */
/*
    在zooKeeper上新建个 /Mutex 目录。
 */
public class TestDistributedLock {
    public static void main(String[] args) {

        // 假定10个线程在跑
        for(int i = 0 ;i < 10;i++){
            final ZkClientExt client = new ZkClientExt("172.18.1.100:2181",
                    5000,5000,new BytesPushThroughSerializer());
//            final SimpleDistributedLockMutex mutex = new SimpleDistributedLockMutex(client,"/Mutex");
            final DistributedLockMutex mutex = new DistributedLockMutex(client,"/Mutex");
            doGetReleaseLock(client,mutex,i);
        }
    }

    private static void doGetReleaseLock(final ZkClientExt client,final DistributedLockMutex mutex,final int i) {
//    private static void doGetReleaseLock(final ZkClientExt client,final SimpleDistributedLockMutex mutex,final int i) {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mutex.acquire();
                        System.out.println("Client" + i + " locked");
                        Thread.sleep((i + 2) * 1000); // 这里就去做你想做的事情
                        mutex.release();
                        System.out.println("Client" + i + " release lock");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
