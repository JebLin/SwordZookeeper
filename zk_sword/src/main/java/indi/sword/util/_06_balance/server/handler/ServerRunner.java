package indi.sword.util._06_balance.server.handler;

import indi.sword.util._06_balance.server.Server;
import indi.sword.util._06_balance.server.ServerData;

import java.util.ArrayList;
import java.util.List;

public class ServerRunner {

    private static final int SERVER_QTY = 2;
    private static final String ZOOKEEPER_SERVER = "172.18.1.100:2181";
    private static final String SERVER_PATH = "/servers";

    public static void main(String[] args) {
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < SERVER_QTY; i++) {
            final Integer count = i;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ServerData serverData = new ServerData();
                    serverData.setBalance(0);
                    serverData.setHost("127.0.0.1");
                    serverData.setPort(6000+count);
                    Server server = new ServerImpl(ZOOKEEPER_SERVER,SERVER_PATH,serverData);
                    server.bind();
                }
            });
            threadList.add(thread);
            thread.start();
        }

        for(int i = 0;i < threadList.size();i++){
            try {
                threadList.get(i).join();
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }
        }
    }
}
