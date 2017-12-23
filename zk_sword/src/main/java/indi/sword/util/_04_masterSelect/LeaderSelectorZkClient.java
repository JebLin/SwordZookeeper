package indi.sword.util._04_masterSelect;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LeaderSelectorZkClient {

    // 启动的客户端 Client 个数
    private static final int CLIENT_QTY = 10;
    // zookeeper 服务器地址
    private static final String ZOOKEEPER_SERVER = "172.18.1.100:2181";

    public static void main(String[] args) {

        // 保存所有zkClient的列表
        List<ZkClient> clients = new ArrayList<>();
        // 保存所有服务的列表
        List<WorkServer> workServers = new ArrayList<>();

        try {
            for(int i = 0;i < CLIENT_QTY; i++){
                // 创建 zkClient
                ZkClient client = new ZkClient(ZOOKEEPER_SERVER,5000,5000,new SerializableSerializer());
                clients.add(client);

                // 创建 ServerData
                RunningData runningData = new RunningData();
                runningData.setCid(Long.valueOf(i));
                runningData.setName("Client #" + i);

                // 创建服务
                WorkServer workServer = new WorkServer(runningData);
                workServer.setZkClient(client);

                workServers.add(workServer);
                workServer.start();
            }
            System.out.println("敲回车键退出！\n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("shutting down...");
            for (WorkServer workServer:workServers){
                try {
                    workServer.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for(ZkClient client : clients){
                try {
                    client.close();
                } catch (ZkInterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }


}
