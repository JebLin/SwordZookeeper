package indi.sword.util._08_queue;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * @Decription 阻塞队列，类似于生产者消费者模式，拿不到就等着取，满了就等着存
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/25 16:51
 */
public class DistributedBlockingQueue<T> extends DistributedSimpleQueue<T> {

    public DistributedBlockingQueue(ZkClient zkClient, String root) {
        super(zkClient, root);
    }

    public T poll() throws Exception{
        while(true){
             final CountDownLatch latch = new CountDownLatch(1);
             final IZkChildListener childListener = new IZkChildListener() {
                 @Override
                 public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                     latch.countDown();
                 }
             };
             zkClient.subscribeChildChanges(root,childListener);
            try {
                T node = super.poll();
                if(node != null){
                    return node;
                }else {
                    latch.await(); // 拿不到我就等着呗 ,super的处理方法是，库存为空就返回 null
                }
            } catch (Exception e) {
                System.out.println("DistributedBlockingQueue poll Exception...");
            } finally {
                zkClient.unsubscribeChildChanges(root,childListener);
            }
        }
    }

}
