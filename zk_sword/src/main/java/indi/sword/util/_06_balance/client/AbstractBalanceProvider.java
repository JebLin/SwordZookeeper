package indi.sword.util._06_balance.client;

import java.util.List;

public abstract class AbstractBalanceProvider<T> implements BalanceProvider<T> {

    protected abstract T balanceAlgorithm(List<T> items);
    protected abstract List<T> getBalanceItems();

    /*
        从全部服务器中，选出一个
     */
    @Override
    public T getBalanceItem(){
        return balanceAlgorithm(getBalanceItems());
    }

}
