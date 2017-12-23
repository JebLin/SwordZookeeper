package indi.sword.util._05_subscribe;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SubscribeZkClient {
    private static final int CLIENT_QTY = 5; // 跑几个客户端
    private static final String ZOOKEEPER_SERVER = "172.18.1.100:2181";
    private static final String CONFIG_PATH = "/config"; // 配置文件config目录
    private static final String COMMAND_PATH = "/command"; // 命令节点，放数据的
    private static final String SERVERS_PATH = "/servers"; // 服务器父节点

    public static void main(String[] args) {
        List<ZkClient> clients = new ArrayList<>();
        List<WorkServer> workServers = new ArrayList<>();
        ManageServer manageServer = null;

        try {
            ServerConfig initConfig = new ServerConfig(); // 这个是要存放的对象
            initConfig.setDbUser("root");
            initConfig.setDbPwd("123456");
            initConfig.setDbUrl("jdbc:mysql://localhost:3306/mydb");

            ZkClient clientManage = new ZkClient(ZOOKEEPER_SERVER,5000,5000,new BytesPushThroughSerializer());
            manageServer = new ManageServer(SERVERS_PATH,COMMAND_PATH,CONFIG_PATH,clientManage,initConfig);
            manageServer.start();

            for(int i = 0;i < CLIENT_QTY ;i++){
                ZkClient client = new ZkClient(ZOOKEEPER_SERVER,5000,5000,new BytesPushThroughSerializer());
                clients.add(client);
                ServerData serverData = new ServerData();
                serverData.setId(i);
                serverData.setName("WorkServer#" + i);
                serverData.setAddress("192.168.1."+i);

                WorkServer workServer = new WorkServer(client,CONFIG_PATH,SERVERS_PATH,serverData,initConfig);
                workServers.add(workServer);
                workServer.start();
            }
            /*
                create /command list
                set /command create
                之后在linux那台zookeeper服务器输入上面两个指令，发送命令
             */
            System.out.println("敲回车键退出！\n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("shutting down ...");
            for (WorkServer workServer:workServers){
                try {
                    workServer.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for(ZkClient client:clients){
                try {
                    client.close();
                } catch (ZkInterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
