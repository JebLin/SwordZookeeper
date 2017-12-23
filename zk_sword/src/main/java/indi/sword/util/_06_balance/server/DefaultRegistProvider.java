package indi.sword.util._06_balance.server;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;

public class DefaultRegistProvider implements RegistProvider {

    @Override
    public void regist(Object context) throws Exception {
        // 1:path
        // 2:zkClient
        // 3:serverData
        ZooKeeperRegistContext registContext = (ZooKeeperRegistContext) context;
        String path = registContext.getPath();
        ZkClient zkClient = registContext.getZkClient();

        try {
            zkClient.createEphemeral(path,registContext.getData()); //创建临时节点
        } catch (ZkNoNodeException e) {
            String parentDir = path.substring(0,path.lastIndexOf('/')); // 没找着爸爸
            zkClient.createPersistent(parentDir,true);
            regist(registContext);
        }

    }

    @Override
    public void unRegist(Object context) throws Exception {

    }
}
