package indi.sword.util._06_balance.client.handler;

import indi.sword.util._06_balance.client.BalanceProvider;
import indi.sword.util._06_balance.client.Client;
import indi.sword.util._06_balance.server.ServerData;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientImpl implements Client {

    private final BalanceProvider<ServerData> provider;
    private EventLoopGroup group = null;
    private Channel channel = null;

    public ClientImpl(BalanceProvider<ServerData> provider) {
        this.provider = provider;
    }

    public BalanceProvider<ServerData> getProvider() {
        return provider;
    }

    @Override
    public void connect() throws Exception {
        try {
            ServerData serverData = provider.getBalanceItem(); // 获取分配到的服务器的 ip port等信息
            System.out.println("connection to " + serverData.getHost() + ":" +
                    serverData.getPort() + ",it's balance:" + serverData.getBalance());

            group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new ClientHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(serverData.getHost(),serverData.getPort()).syncUninterruptibly();
            channel = channelFuture.channel();
            System.out.println("started success !");

        } catch (Exception e) {
            System.out.println("连接异常:"+e.getMessage());
        }

    }

    @Override
    public void disConnect() throws Exception {
        try {
            if(channel  != null){
                channel.close().syncUninterruptibly();
            }
            group.shutdownGracefully();
            group = null;
            System.out.println("disconnected !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
