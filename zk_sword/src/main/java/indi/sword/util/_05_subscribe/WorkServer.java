package indi.sword.util._05_subscribe;

import com.google.gson.Gson;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.KeeperException;

public class WorkServer {

    private ZkClient zkClient;
    private String configPath;
    private String serverPath;
    private ServerData serverData;
    private ServerConfig serverConfig;
    private IZkDataListener dataListener;

    private static final Gson GSON = new Gson();

    public WorkServer(ZkClient zkClient, String configPath, String serverPath, ServerData serverData, ServerConfig serverConfig) {
        this.zkClient = zkClient;
        this.configPath = configPath;
        this.serverPath = serverPath;
        this.serverData = serverData;
        this.serverConfig = serverConfig;
        this.dataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String path, Object data) throws Exception {
                String retJson = new String((byte[])data);
                ServerConfig serverConfigLocal = GSON.fromJson(retJson, ServerConfig.class);
                System.out.println("serverConfigLocal == " + serverConfigLocal);
                updateConfig(serverConfigLocal);
                System.out.println("new work server config is :" + serverConfig.toString());

            }

            @Override
            public void handleDataDeleted(String s) throws Exception {

            }
        };
    }

    public void start(){
        System.out.println("work server start ...");
        initRunning();
    }

    private void initRunning() {
        registMe();
        zkClient.subscribeDataChanges(configPath,dataListener);
    }

    private void registMe() {
        String mePath = serverPath.concat("/").concat(serverData.getAddress());
        try {
            zkClient.createEphemeral(mePath,GSON.toJson(serverData).getBytes());
        } catch (ZkNoNodeException e) {
            zkClient.createPersistent(serverPath,true);
            registMe();
        }

/*
    创建永久节点
 */
//        try {
//            zkClient.createPersistent(mePath,GSON.toJson(serverData).getBytes());
//        } catch (ZkNoNodeException e) {
//            zkClient.createPersistent(serverPath,true);
//            registMe();
//        }
//        catch (ZkNodeExistsException e){
//            zkClient.deleteRecursive(serverPath); // 我这里创建的是永久节点，没法重复测试，我就删了再添加
//            registMe();
//        }
    }

    public void stop(){
        System.out.println("work server stop ...");
        zkClient.unsubscribeDataChanges(configPath,dataListener);
    }

    private void updateConfig(ServerConfig serverConfigLocal) {
        this.serverConfig = serverConfigLocal;
    }

    @Override
    public String toString() {
        return "WorkServer{" +
                "zkClient=" + zkClient +
                ", configPath='" + configPath + '\'' +
                ", serverPath='" + serverPath + '\'' +
                ", serverData=" + serverData +
                ", serverConfig=" + serverConfig +
                ", dataListener=" + dataListener +
                '}';
    }
}
