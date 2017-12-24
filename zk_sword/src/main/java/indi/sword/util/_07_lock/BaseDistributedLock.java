package indi.sword.util._07_lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Decription 基本分布式锁
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/24 14:59
 */
public class BaseDistributedLock {

    private final ZkClientExt zkClientExt;
    private final String path; // 子节点的完整路径
    private final String basePath; // 根节点
    private final String lockName; // 要加锁的节点的名称
    private static final Integer MAX_RETRY_COUNT = 10;

    public BaseDistributedLock(ZkClientExt zkClientExt, String basePath, String lockName) {
        this.zkClientExt = zkClientExt;
        this.basePath = basePath;
        this.path = basePath.concat("/").concat(lockName);
        this.lockName = lockName;
    }

    /**
     * @Decription 删除节点
     * @Author: rd_jianbin_lin
     * @Date : 2017/12/24 12:33
     */
    private void deletePath(String path) throws Exception{
        zkClientExt.delete(path);
    }

    /**
     * @Decription 创建临时的，序列化节点
     *              例如：节点分别为/lock/0000000001、/lock/0000000002、/lock/0000000003。
     * @Author: rd_jianbin_lin
     * @Date : 2017/12/24 12:34
     */
    private String createLockNode(ZkClient zkClient,String path) throws Exception{
        return zkClient.createEphemeralSequential(path,null);
    }

    /**
     * @Decription 释放锁
     * @Author: rd_jianbin_lin
     * @Date : 2017/12/24 15:06
     */
    protected void releaseLock(String lockPath) throws Exception{
        deletePath(lockPath); // 释放锁，也就删除当下节点，那么下面的节点就可以获取到锁了
    }

    /**
     * @Decription 尝试上锁
     * @Author: rd_jianbin_lin
     * @Date : 2017/12/24 15:32
     * @param time: 等待的时间，
     * @param timeUnit: 等待时间的单位，null 表示一直等待
     */
    protected String attemptLock(long time,TimeUnit timeUnit) throws Exception{
        final long startMillis = System.currentTimeMillis();
        final Long millisToWait = (timeUnit != null) ? timeUnit.toMillis(time) : null; // 转换成毫秒数 // timeUnit == null的话，表明是想等待一辈子
        String     ourPath = null; // 要求锁的节点路径
        boolean    hasTheLock = false; // 是否拥有锁
        boolean    isDone = false; // 任务是否完成
        int        retryCount = 0;  // 尝试获取锁的次数

        //网络闪断需要重试一试
        while (!isDone){
            isDone = true;

            try {
                ourPath = createLockNode(zkClientExt,path);
                hasTheLock = waitToLock(startMillis,millisToWait,ourPath);
            } catch (ZkNoNodeException e) {
                if(retryCount++ < MAX_RETRY_COUNT){
                    isDone = false; // 再次尝试
                }else{
                    throw e;
                }
            }
        }
        if(hasTheLock){ // 拿到锁了，返回当前node路径
            return ourPath;
        }
        return null;
    }

    /**
     * @Decription 等待直到获取锁
     * @Author: rd_jianbin_lin
     * @Date : 2017/12/24 14:38
     * @param millisToWait : 等待的时间,时间一到就放弃，就不去获取锁了。
     *                     millisToWait == null的话，表明是想等待一辈子
     * @param ourPath :   锁住的某个节点
     */
    private boolean waitToLock(long startMillis,Long millisToWait,String ourPath) throws Exception{
        boolean haveTheLock = false; //是否拥有锁
        boolean doDeleteOurPath = false; // 是否不去抢锁了，删掉自己的节点

        try {
            while( !haveTheLock){ // 一直尝试拿到锁，haveTheLock = true才结束方法
                List<String> childrenNameList = getSortedChildren(); // 得到排序好的子节点名称
                String sequenceNodeName = ourPath.substring(basePath.length() + 1); // 要拿锁的那个子节点名称
                int ourIndex = childrenNameList.indexOf(sequenceNodeName); // List的 indexOf 找到下标
                if(ourIndex < 0){
                    throw new ZkNoNodeException("节点没有找到：" + sequenceNodeName);
                }
                boolean isGetTheLock = ourIndex == 0; // 要锁的那个路径ourPath的下标是不是0，是0的话，就说明已经拿到锁了。
                String pathToWatch = isGetTheLock ? null : childrenNameList.get(ourIndex - 1); // 如果没拿到锁，那就监视上一个节点，不监听全部节点，避免羊群效应

                if(isGetTheLock){ // 如果拿到锁了
                    haveTheLock = true;
                }else{
                    String previousSequencePath = basePath.concat("/").concat(pathToWatch); // 前一个节点路径
                    final CountDownLatch latch = new CountDownLatch(1);// countDownLatch 用于控制进程结束时间
                    final IZkDataListener previousListener = new IZkDataListener() {
                        @Override
                        public void handleDataChange(String dataPath, Object data) throws Exception {
                            // ignore
                        }

                        @Override
                        public void handleDataDeleted(String dataPath) throws Exception { // 节点删除的时候，就退出
                            latch.countDown(); // 线程可以退出等待了，可以往下执行拿锁草错了

                        }
                    };
                    try {
                        // 如果节点不存在就会抛出 ZkNoNodeException
                        zkClientExt.subscribeDataChanges(previousSequencePath,previousListener); // zkClient 见监听上一节点的事件变化（用某一处理器监听某一节点）

                        if( millisToWait != null){ // 还得等多久？（等待锁的时间）
                            millisToWait -= (System.currentTimeMillis() - startMillis);
                            startMillis = System.currentTimeMillis();
                            if(millisToWait <= 0){ //timeout,等不及了
                                doDeleteOurPath = true; // 最多等你那么久，不等了，我删了自己要等待的节点
                                break;
                            }
                            latch.await(millisToWait, TimeUnit.MILLISECONDS); // 线程等待时间，时间到了就往下走
                        }else{  // millisToWait == null的话，表明是想等待一辈子
                            latch.await();
                        }
                    } catch (ZkNoNodeException e) {
                        System.out.println("waitToLock 没找到节点...");
                    } finally {
                        zkClientExt.unsubscribeDataChanges(previousSequencePath,previousListener); // 最终结束后，要取消消息订阅
                    }
                }
            }
        } catch (Exception e) {
            // 发生异常，删除掉节点
            doDeleteOurPath = true;
            throw e; // 不处理，上面去处理

        } finally {
             // 如果需要删除节点
            if(doDeleteOurPath){
                deletePath(ourPath);
            }
        }
        return haveTheLock; // 这里肯定是true
    }

    /**
     * @Decription 得到排序好的，从小到大排序
     * @Author: rd_jianbin_lin
     * @Date : 2017/12/24 13:12
     */
    public List<String> getSortedChildren() {
        try {
            List<String> children = zkClientExt.getChildren(basePath); // 找到basePath 目录下的所有子节点
            Collections.sort(children, new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return getLockNodeNumber(lhs,lockName).compareTo(getLockNodeNumber(rhs,lockName));
                }
            });
            return children;
        } catch (ZkNoNodeException e) {
            zkClientExt.createPersistent(basePath,true);
            return getSortedChildren();
        }
    }

    /**
     * 得到锁住的节点node，最小的那个肯定是被锁住的那个。
     * 因为是临时的序列化节点，所以都是node名称都是按序号排序的
     * @Decription
     * @Author: rd_jianbin_lin
     * @Date : 2017/12/24 13:10
     */
    private String getLockNodeNumber(String str, String lockName) {
        int index = str.lastIndexOf(lockName);
        if(index >= 0){
            index += lockName.length();
            return index <= str.length() ? str.substring(index) : "";
        }
        return str;
    }
}
