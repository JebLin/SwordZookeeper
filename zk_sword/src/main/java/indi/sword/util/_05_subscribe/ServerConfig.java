package indi.sword.util._05_subscribe;

/**
 * @Decription  放数据的
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/23 12:07
 */
public class ServerConfig {

    private String dbUrl;
    private String dbPwd;
    private String dbUser;

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbPwd() {
        return dbPwd;
    }

    public void setDbPwd(String dbPwd) {
        this.dbPwd = dbPwd;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "dbUrl='" + dbUrl + '\'' +
                ", dbPwd='" + dbPwd + '\'' +
                ", dbUser='" + dbUser + '\'' +
                '}';
    }
}
