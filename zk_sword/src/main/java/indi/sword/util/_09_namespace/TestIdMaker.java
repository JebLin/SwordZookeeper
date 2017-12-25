package indi.sword.util._09_namespace;

/**
 * @Decription 分布式生成ID，由于就算删了节点，新增的节点也是继续往下走的，不会存在重复
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/25 17:59
 */
public class TestIdMaker {
    public static void main(String[] args) throws Exception {

        IdMaker idMaker = new IdMaker("172.18.1.100:2181",
                "/NameService/IdGen", "ID");
        idMaker.start();

        try {
            for (int i = 0; i < 10; i++) {
                String id = idMaker.generateId(IdMaker.RemoveMethod.DELAY);
                System.out.println(id);

            }
        } finally {
            idMaker.stop();

        }
    }

}
