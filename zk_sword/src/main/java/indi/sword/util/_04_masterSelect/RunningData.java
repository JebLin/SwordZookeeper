package indi.sword.util._04_masterSelect;

import java.io.Serializable;

/**
 * 服务器配置
 * @Decription
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/23 9:02
 */
public class RunningData implements Serializable {
    private static final long serialVersionUID = -4813828297914447473L;

    private Long cid;

    private String name;

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RunningData{" +
                "cid=" + cid +
                ", name='" + name + '\'' +
                '}';
    }
}
