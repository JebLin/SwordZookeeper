package indi.sword.util._07_lock;

import java.util.concurrent.TimeUnit;

/**
 * @Decription
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/24 12:14
 */
public interface DistributedLock {

    /*
        获取锁，如果没有得到就等待
     */
    void acquire() throws Exception;

    /*
        获取锁，直到超时
     */
    boolean acquire(long time, TimeUnit unit) throws Exception;

    /*
        释放锁
     */
    void release() throws Exception;
}
