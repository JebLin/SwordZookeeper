package indi.sword.util._06_balance.server;

import java.io.Serializable;

public class ServerData implements Serializable,Comparable<ServerData> {

    private static final long serialVersionUID = 8360016260946961228L;

    private Integer balance;
    private String host;
    private Integer port;

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public int compareTo(ServerData o) {
        return this.getBalance().compareTo(o.getBalance());
    }

    @Override
    public String toString() {
        return "ServerData{" +
                "balance=" + balance +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
