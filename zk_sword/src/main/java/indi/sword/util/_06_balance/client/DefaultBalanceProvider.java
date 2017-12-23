package indi.sword.util._06_balance.client;

import indi.sword.util._06_balance.server.ServerData;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Decription 默认的负载均衡提供器
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/23 14:50
 */
public class DefaultBalanceProvider extends AbstractBalanceProvider<ServerData> {

    private final String zkServer;
    private final String serverPath;
    private final ZkClient zkClient;

    private static final Integer SESSION_TIME_OUT = 10000;
    private static final Integer CONNECT_TIME_OUT = 10000;

    public DefaultBalanceProvider(String zkServer, String serverPath) {
        this.zkServer = zkServer;
        this.serverPath = serverPath;
        this.zkClient = new ZkClient(this.zkServer,SESSION_TIME_OUT,CONNECT_TIME_OUT,new SerializableSerializer());
    }

    @Override
    protected ServerData balanceAlgorithm(List<ServerData> items) {
        if(items.size() > 0){
            Collections.sort(items);
            return items.get(0);
        }else{
            return null;
        }
    }

    /*
        拿到所有工作服务器的列表
     */
    @Override
    protected List<ServerData> getBalanceItems() {
        
        List<ServerData> serverDataList = new ArrayList<ServerData>();
        List<String> childrenPathList = zkClient.getChildren(this.serverPath);
        for (String childrenPath :
                childrenPathList) {
            ServerData serverData = zkClient.readData(serverPath + "/" + childrenPath);
            serverDataList.add(serverData);
        }
        return serverDataList;
    }
}
