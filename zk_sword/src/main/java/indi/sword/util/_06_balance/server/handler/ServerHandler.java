package indi.sword.util._06_balance.server.handler;

import indi.sword.util._06_balance.server.BalanceUpdateProvider;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Decription 这个是用来控制权重的，放在 ServerImpl 类里面
 * @Author: rd_jianbin_lin
 * @Date : 2017/12/23 16:47
 */
public class ServerHandler extends ChannelHandlerAdapter {

    private final BalanceUpdateProvider balanceUpdater;
    private static final Integer BALANCE_STEP = 1; // 每次增加的balance量


    public ServerHandler(BalanceUpdateProvider balanceUpdater){
        this.balanceUpdater = balanceUpdater;

    }

    public BalanceUpdateProvider getBalanceUpdater() {
        return balanceUpdater;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("one client connect...");
        balanceUpdater.addBalance(BALANCE_STEP);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        balanceUpdater.reduceBalance(BALANCE_STEP);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
