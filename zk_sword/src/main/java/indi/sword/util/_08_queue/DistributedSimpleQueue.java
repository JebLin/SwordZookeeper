package indi.sword.util._08_queue;

import org.I0Itec.zkclient.ExceptionUtil;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Decription 分布式队列
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/25 16:17
 */
public class DistributedSimpleQueue<T> {

    protected final ZkClient zkClient;
    protected final String root;

    protected static final String NODE_NAME = "n_";

    public DistributedSimpleQueue(ZkClient zkClient, String root) {
        this.zkClient = zkClient;
        this.root = root;
    }

    public int size(){
        return zkClient.getChildren(root).size();
    }

    public boolean isEmpty(){
        return size() == 0 ;
    }

    /**
     * @Decription 插入数据到队列 FIFO
     * @Author: rd_jianbin_lin
     * @Date : 2017/12/25 16:17
     */
    public boolean offer(T element) throws Exception{
        String nodeFullPath = root.concat("/").concat(NODE_NAME);
        try {
            zkClient.createPersistentSequential(nodeFullPath,element);
        } catch (ZkNoNodeException e){
            zkClient.createPersistent(root); // 找不到爸爸，就建立个爸爸
            offer(element);
        } catch (Exception e){
            throw ExceptionUtil.convertToRuntimeException(e); //程序终止
        }
        return true;

    }

    /**
     * @Decription 出队列，FIFO
     * @Author: rd_jianbin_lin
     * @Date : 2017/12/25 16:12
     */
    @SuppressWarnings("unchecked")
    public T poll() throws Exception{
        try {
            List<String> list = zkClient.getChildren(root);
            if(list.size() == 0){
                return null;
            }
            Collections.sort(list, new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return getNodeNumber(lhs, NODE_NAME).compareTo(getNodeNumber(rhs,NODE_NAME))  ;
                }
            });

            for(String nodeName : list){
                try {
                    String nodeFullPath = root.concat("/").concat(nodeName);
                    T node = (T)zkClient.readData(nodeFullPath);
                    zkClient.delete(nodeFullPath);
                    return node;
                } catch (ZkNoNodeException e) {
                    System.out.println("DistributedSimpleQueue poll ZkNoNodeException ...");
                }
            }
            return null;
        } catch (Exception e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
    }

    /**
     * @Decription n_00000001 n_00000002 把前缀 n_ 去掉然后进行比较
     * @Author: rd_jianbin_lin
     * @Date : 2017/12/25 15:58
     */
    private String getNodeNumber(String str, String nodeName) {
        int index = str.lastIndexOf(nodeName);
        if(index >= 0){
            index += NODE_NAME.length();
            return index <= str.length() ? str.substring(index) : "";
        }
        return str;
    }



}
