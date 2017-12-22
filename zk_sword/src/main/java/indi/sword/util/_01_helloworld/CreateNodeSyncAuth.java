package indi.sword.util._01_helloworld;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * @Decription 重要！重要！重要！
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/22 16:09
 */
public class CreateNodeSyncAuth implements Watcher{

    private static ZooKeeper zooKeeper;

    private static boolean somethingDone = false;

    public static void main(String[] args) throws InterruptedException, IOException, NoSuchAlgorithmException {
        zooKeeper = new ZooKeeper("172.18.1.100:2181",5000,new CreateNodeSyncAuth());
        System.out.println(zooKeeper.getState());
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("收到事件：" + watchedEvent);
        if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
            if(!somethingDone && watchedEvent.getType() == Event.EventType.None && null == watchedEvent.getPath()){
                doSomething();
            }
        }
    }

    /**
     * @Decription 带权限创建节点，只有有足够权限才能对该节点采取操作
     * @Author: rd_jianbin_lin
     * @Date : 2017/12/22 16:10
     */
    private void doSomething() {
        try {
            ACL aclIp = new ACL(ZooDefs.Perms.READ,new Id("ip","172.18.1.100")); // 只能通过 172.18.1.100 这个地址的 ip 去访问
            ACL aclDigest = new ACL(ZooDefs.Perms.READ | ZooDefs.Perms.WRITE,new Id("digest",
                    DigestAuthenticationProvider.generateDigest("jike:123456"))); // 可以通过账号密码访问

            ArrayList<ACL> acls = new ArrayList<>();
            acls.add(aclDigest);
            acls.add(aclIp);

            String path = zooKeeper.create("/node_666","666".getBytes(),acls, CreateMode.PERSISTENT);
            /*
                创建完 /node_666 ,你尝试用 172.18.2.112 的机器去 zkCli.sh -server localhost:2181
                去 get /node_666 的时候，会提示无权限“Authentication is not valid : /node_666”.
                可以在 112 的控制台输入 addauth digest jike:123456 然后再get就OK了
             */
            System.out.println(path);

            somethingDone = true;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }
}
