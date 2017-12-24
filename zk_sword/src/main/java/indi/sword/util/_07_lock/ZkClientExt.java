package indi.sword.util._07_lock;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.Callable;

public class ZkClientExt extends ZkClient {

    public ZkClientExt(String zkServers, int sessionTimeout, int connectionTimeout, ZkSerializer zkSerializer) {
        super(zkServers, sessionTimeout, connectionTimeout, zkSerializer);
    }

    /**
     * @Decription subscribeDataChanges 用到改方法
     * @Author: rd_jianbin_lin
     * @Date : 2017/12/24 12:28
     */
    public void watchForData(final String path){
        retryUntilConnected(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Stat stat = new Stat();
                _connection.readData(path,stat,true);
                return null;
            }
        });
    }
}
