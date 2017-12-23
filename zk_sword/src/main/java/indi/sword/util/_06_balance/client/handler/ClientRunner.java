package indi.sword.util._06_balance.client.handler;

import indi.sword.util._06_balance.client.BalanceProvider;
import indi.sword.util._06_balance.client.Client;
import indi.sword.util._06_balance.client.DefaultBalanceProvider;
import indi.sword.util._06_balance.server.ServerData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ClientRunner {
    private static final int CLIENT_QTY = 5; // 开5个客户端去连接，测试负载均衡
    private static final String ZOOKEEPER_SERVER = "172.18.1.100:2181";
    private static final String SERVER_PATH = "/servers";

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threadList = new ArrayList<>();
        final List<Client> clientList = new ArrayList<>();  // 客户端列表
        final BalanceProvider<ServerData> balanceProvider =
                new DefaultBalanceProvider(ZOOKEEPER_SERVER,SERVER_PATH); // 全部客户端用同一个 balanceProvider 负载均衡器，由一个来进行调度
        try {
            for(int i =0 ;i < CLIENT_QTY;i++){
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Client client = new ClientImpl(balanceProvider); //
                        clientList.add(client); // clientList 用来关闭资源的
                        try {
                            client.connect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                threadList.add(thread);
                thread.start();
                Thread.sleep(2000);
            }

            System.out.println("敲回车键退出！ \n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            for(int i = 0;i< clientList.size();i++){
                try {
                    clientList.get(i).disConnect();
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
            for(int i = 0;i< threadList.size();i++){
                threadList.get(i).interrupt();
                try {
                    threadList.get(i).join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
