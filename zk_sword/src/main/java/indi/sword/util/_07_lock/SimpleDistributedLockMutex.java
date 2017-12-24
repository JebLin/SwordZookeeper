package indi.sword.util._07_lock;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Mutex : 互斥
 * @Decription 简单分布式互斥锁
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/24 16:06
 */
public class SimpleDistributedLockMutex extends BaseDistributedLock implements DistributedLock{

    // 锁名称前缀
    private static final String LOCK_NAME_PREFIX = "lock-"; // 待会的节点长这样 [lock-0000000009, lock-0000000008]

    private final String basePath; // 基本路径

    private String ourLockPath; // 锁的子节点的路径

    public SimpleDistributedLockMutex(ZkClientExt zkClientExt, String basePath) {
        super(zkClientExt, basePath, LOCK_NAME_PREFIX);
        this.basePath = basePath;
    }

    /**
     * @Decription
     * @Author: rd_jianbin_lin
     * @Date : 2017/12/24 16:04
     * @param time: 等待的时间，
     * @param timeUnit: 等待时间的单位，null 表示一直等待
     */
    private boolean internalLock(long time,TimeUnit timeUnit) throws Exception{
        ourLockPath = attemptLock(time,timeUnit);
        return ourLockPath != null;
    }

    /**
     * @Decription 获取锁
     * @Author: rd_jianbin_lin
     * @Date : 2017/12/24 16:04
     */
    @Override
    public void acquire() throws Exception {
        if( !internalLock(-1,null)){
            throw new IOException("连接丢失!在路径:'"+basePath+"'下不能获取锁!");
        }
    }

    @Override
    public boolean acquire(long time, TimeUnit unit) throws Exception {
        return internalLock(time,unit);
    }

    @Override
    public void release() throws Exception {
        releaseLock(ourLockPath);
    }
}
