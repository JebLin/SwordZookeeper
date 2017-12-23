package indi.sword.util._05_subscribe;

import com.google.gson.Gson;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.List;

public class ManageServer {
    private String serversPath;
    private String commandpath;
    private String configPath;
    private ZkClient zkClient;
    private ServerConfig serverConfig;
    private IZkChildListener childListener;
    private IZkDataListener dataListener;
    private List<String> workServerList;

    private static final Gson GSON = new Gson();

    public ManageServer(String serversPath, String commandpath, String configPath, ZkClient zkClient, ServerConfig serverConfig) {
        this.serversPath = serversPath;
        this.commandpath = commandpath;
        this.configPath = configPath;
        this.zkClient = zkClient;
        this.serverConfig = serverConfig;
        this.childListener = new IZkChildListener() {
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
        zkClient.unsubscribeDataChanges(commandpath,dataListener);
    }

    private void initRunning() {
        zkClient.subscribeDataChanges(commandpath,dataListener); // 对 commandpath 路径发生的数据变化进行监听。
        zkClient.subscribeChildChanges(serversPath,childListener); // 对 serversPath 路径下的孩子变化进行监听。
    }

}
