package indi.sword.util._07_lock;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mutex : 互斥
 * @Decription 分布式互斥锁
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/24 16:39
 */
public class DistributedLockMutex extends BaseDistributedLock implements DistributedLock{

    // 锁名称前缀
    private static final String LOCK_NAME_PREFIX = "lock-";

    // 进行中的线程信息
    private final ConcurrentMap<Thread,LockData> threadLockDataMap = new ConcurrentHashMap<>();

    private final String basePath; // 根节点路径

    private static class LockData{ // 一个线程只能锁住一个节点
        final String lockPath; // 上锁的节点路径
        final AtomicInteger lockCount = new AtomicInteger(1); // 当前线程上锁的次数

        public LockData(Thread owningThread,String lockPath) {
            this.lockPath = lockPath;
        }
    }

    public DistributedLockMutex(ZkClientExt zkClientExt, String basePath) {
        super(zkClientExt, basePath, LOCK_NAME_PREFIX); // 根节点 + 锁名称前缀
        this.basePath = basePath;
    }

    private boolean internalLock(long time,TimeUnit timeUnit) throws Exception{
        Thread currentThread = Thread.currentThread();
        LockData lockData = threadLockDataMap.get(currentThread);
        if(lockData != null){ // 该线程已经上过一次锁了。
            lockData.lockCount.incrementAndGet(); // 锁次数加一。 注意： incrementAndGet 与 getAndIncrement 的返回值不一样，注意看方法注释
            return true;
        }else{ // 该线程还没上过锁
            String lockPath = attemptLock(time,timeUnit); // 获取锁,拿不到的话，返回是 null
            if ( lockPath != null )
            {
                LockData newLockData = new LockData(currentThread,lockPath);
                threadLockDataMap.put(currentThread,newLockData);
                return true;
            }else {
                return false;
            }
        }
    }

    @Override
    public void acquire() throws Exception {
        if( !internalLock(-1,null)){
            throw new IOException("连接丢失!在路径:'"+basePath+"'下不能获取锁!");
        }
    }

    @Override
    public boolean acquire(long time, TimeUnit unit) throws Exception {

        return internalLock(time, unit);
    }

    @Override
    public void release() throws Exception { // 以后可以尝试传进来某一个 nodePath ,释放线程锁住的某一个node
        Thread currentThread = Thread.currentThread();

        LockData lockData = threadLockDataMap.get(currentThread);
        if(lockData == null){
            throw new IllegalMonitorStateException("你不是锁: " + basePath + "的拥有者,无法执行此操作！");
        }
        /*
            获取数量减一后的值域。
            如果当前线程只锁住了一次，那么待会结果会等于0，可以释放删除掉。
            如果当前线程锁住了多次，那么结果将会大于0，大于0的话，说明该节点该线程还用着，不能释放删除掉。

         */
        int newLockCount = lockData.lockCount.decrementAndGet(); // 数量-1
        if(newLockCount > 0){ // 如果当前线程锁住了多次，那么结果将会大于0
            return; // 直接返回就是了,不用remove掉map里面的thread
        }else if(newLockCount < 0){ // 正常情况下，不可能小于0
            throw new IllegalMonitorStateException("锁计数器已经为负数: " + basePath);
        }else {
            try {
                releaseLock(lockData.lockPath);
            }finally {
                threadLockDataMap.remove(currentThread);
            }
        }
    }
}
