package indi.sword.util._04_masterSelect;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkException;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WorkServer {

    private volatile boolean running = false;

    private ZkClient zkClient;

    private static final String MASTER_PATH = "/master"; // master 节点路径

    private IZkDataListener dataListener; // 节点数据监听器

    private RunningData serverData; // 当前 server 服务器的数据

    private RunningData masterData; // master 服务器的数据

    private ScheduledExecutorService delayExecutor = Executors.newScheduledThreadPool(1); // 线程池，用于延时启动使用

    private int delayTime = 5;

    public WorkServer(RunningData rd) {
        this.serverData = rd;
        this.dataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {

            }

            @Override
            public void handleDataDeleted(String path) throws Exception {
                if(masterData != null && masterData.getName().equals(serverData.getName())){ // 如果master节点被当前server占有的话，那就优先争夺控制权
                    takeMaster();
                }else{ // 若master不是老机器占有的话，延迟5秒再去抢夺master
                    delayExecutor.schedule(new Runnable() {
                        @Override
                        public void run() {
                            takeMaster();
                        }
                    },delayTime, TimeUnit.SECONDS);
                }
            }
        };
    }

    /**
     * @Decription 争抢Master
     * @Author: rd_jianbin_lin
     * @Date : 2017/12/23 8:48
     */
    private void takeMaster() {
        if (!running)
            return;

        try {
            zkClient.create(MASTER_PATH, serverData, CreateMode.EPHEMERAL);
            masterData = serverData;
            System.out.println(serverData.getName()+" is master");
            delayExecutor.schedule(new Runnable() {
                public void run() {
                    // TODO Auto-generated method stub
                    if (checkMaster()){
                        releaseMaster();
                    }
                }
            }, 5, TimeUnit.SECONDS);

        } catch (ZkNodeExistsException e) {
            RunningData runningData = zkClient.readData(MASTER_PATH, true);
            if (runningData == null) {
                takeMaster();
            } else {
                masterData = runningData;
            }
        } catch (Exception e) {
            // ignore;
        }

    }

    /**
     * @Decription 释放Master
     * @Author: rd_jianbin_lin
     * @Date : 2017/12/23 8:50
     */
    private void releaseMaster() {
        if(checkMaster()){
            System.out.println("delete master " + serverData);
            zkClient.delete(MASTER_PATH);
        }
    }

    /**
     * @Decription 检查Master
     * @Author: rd_jianbin_lin
     * @Date : 2017/12/23 8:48
     */
    private boolean checkMaster() {
        try {
            RunningData eventData = zkClient.readData(MASTER_PATH);
            masterData = eventData;
            if (masterData.getName().equals(serverData.getName())) { // 如果master是当前server的话
                return true;
            }
            return false;
        } catch (ZkNoNodeException e) {
            return false;
        } catch (ZkInterruptedException e) {
            return checkMaster();
        } catch (ZkException e) {
            return false;
        }
    }

    public void start() throws Exception{
        if(running){
            throw new Exception("server has startup");
        }
        running = true;
        zkClient.subscribeDataChanges(MASTER_PATH,dataListener);
        takeMaster();
    }

    public void stop() throws Exception{
        if(!running){
            throw new Exception("server has stoped");
        }
        running = false;
        delayExecutor.shutdown();
        zkClient.unsubscribeDataChanges(MASTER_PATH,dataListener);
        releaseMaster();
    }

    public ZkClient getZkClient() {
        return zkClient;
    }

    public void setZkClient(ZkClient zkClient) {
        this.zkClient = zkClient;
    }
}
