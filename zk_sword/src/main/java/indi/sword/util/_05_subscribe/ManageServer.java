    package indi.sword.util._05_subscribe;

import com.google.gson.Gson;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.List;

/**
 * @Decription 管理类，监听都写在这里
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/23 12:34
 */
public class ManageServer {
    private String serversPath; // 服务器 server 的父节点路径
    private String commandPath; // 命令路径
    private String configPath; // 配置文件存放的地点
    private ZkClient zkClient;
    private ServerConfig serverConfig; // 服务器的详细配置
    private IZkChildListener childListener; // node child 监听器
    private IZkDataListener dataListener; // node data 监听器
    private List<String> workServerList; // 正在工作的服务器列表 list

    private static final Gson GSON = new Gson();

    public ManageServer(String serversPath, String commandPath, String configPath, ZkClient zkClient, ServerConfig serverConfig) {
        this.serversPath = serversPath;
        this.commandPath = commandPath;
        this.configPath = configPath;
        this.zkClient = zkClient;
        this.serverConfig = serverConfig;
        this.childListener = new IZkChildListener() {
            /*
                一有人去注册节点，那么就说明有新的服务器加入了。
                或者说有服务器宕机了，也会触发 ChildChange 事件
             */
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                workServerList = currentChilds;
                System.out.print("work server list changed ,new list is ");
                execList();
            }
        };
        this.dataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                String cmd = new String((byte[]) data);
                System.out.println("cmd : " + cmd);
                execCmd(cmd);
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {

            }
        };

    }

    private void execCmd(String cmdType) {
        if("list".equals(cmdType)){
            execList();
        }else if("create".equals(cmdType)){
            execCreate();
        }else if("modify".equals(cmdType)){
            execModify();
        }else{
            System.out.println("error command !" + cmdType);
        }
    }

    private void execModify() {
        serverConfig.setDbUser(serverConfig.getDbUser() + "_modify");
        try {
            zkClient.writeData(configPath,GSON.toJson(serverConfig).getBytes());
        } catch (ZkNoNodeException e) {
            e.printStackTrace();
        }
    }

    private void execCreate() {
        try {
            if(!zkClient.exists(configPath)){
                zkClient.createPersistent(configPath,GSON.toJson(serverConfig).getBytes());
            }
        } catch (ZkNodeExistsException e) {
            zkClient.writeData(configPath,GSON.toJson(serverConfig).getBytes());
        } catch (ZkNoNodeException e){
            String parentDir = configPath.substring(0,configPath.lastIndexOf('/'));
            zkClient.createPersistent(parentDir,true);
            execCreate();
        }
    }

    private void execList() {
        if(null != workServerList){
            System.out.println(workServerList.toString());
        }
    }
    
    public void start(){
        initRunning();
    }

    public void stop(){
        zkClient.unsubscribeChildChanges(serversPath,childListener);
        zkClient.unsubscribeDataChanges(commandPath,dataListener);
    }

    /*
        待会呢，在linux那台zookeeper服务器输入上面两个指令，发送命令，会触发监听器
           create /command list
           set /command create
    */
    private void initRunning() {
        zkClient.subscribeDataChanges(commandPath,dataListener); // 对 commandPath 路径发生的数据变化进行监听。
        zkClient.subscribeChildChanges(serversPath,childListener); // 对 serversPath 路径下的孩子变化进行监听。
    }

}
