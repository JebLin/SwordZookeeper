package indi.sword.util._06_balance.server;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkBadVersionException;
import org.apache.zookeeper.data.Stat;

public class DefaultBalanceUpdateProvider implements BalanceUpdateProvider {

    private String serverPath;
    private ZkClient zkClient;

    public DefaultBalanceUpdateProvider(String serverPath, ZkClient zkClient) {
        this.serverPath = serverPath;
        this.zkClient = zkClient;
    }

    @Override
    public boolean addBalance(Integer step) {

        Stat stat = new Stat();
        ServerData serverData;
        while(true){
            try {
                serverData = zkClient.readData(this.serverPath,stat); // 读出数据，并且把改节点状态写到 stat里面去。
                System.out.println("addBalance stat = " + stat);
                serverData.setBalance(serverData.getBalance() + step);
                zkClient.writeData(this.serverPath,serverData,stat.getVersion()); //  CAS 写
                return true;
            } catch (ZkBadVersionException e) {
                System.out.println("someone modify the data ...");
            } catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public boolean reduceBalance(Integer step) {
        Stat stat = new Stat();
        ServerData serverData;
        while(true){
            try {
                serverData = zkClient.readData(this.serverPath,stat);
                System.out.println("reduceBalance stat = " + stat);

                final Integer currBalance = serverData.getBalance();
                serverData.setBalance(currBalance > step ? currBalance - step : 0);
                zkClient.writeData(this.serverPath,serverData,stat.getVersion());
                return true;
            } catch (ZkBadVersionException e) {
                System.out.println("someone modify the data ...");
            } catch (Exception e){
                return false;
            }
        }
    }
}
