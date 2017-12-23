package indi.sword.util._06_balance.server;

public interface BalanceUpdateProvider {

    public boolean addBalance(Integer step);

    public boolean reduceBalance(Integer step);

}
