package indi.sword.util._06_balance.server.handler;

import indi.sword.util._06_balance.server.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

public class ServerImpl implements Server {

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workGroup = new NioEventLoopGroup();
    private ServerBootstrap bootstrap = new ServerBootstrap();
    private ChannelFuture channelFuture;
    private String zkAddres;
    private String serversPath;
    private String currentServerPath;
    private ServerData serverData;

    private volatile boolean binded = false; // 已经绑定了没有

    private final ZkClient zkClient;
    private final RegistProvider registProvider;

    private static final Integer SESSION_TIME_OUT = 10000;
    private static final Integer CONNECTION_TIME_OUT = 10000;

    public ServerImpl(String zkAddres, String serversPath, ServerData serverData) {
        this.zkAddres = zkAddres;
        this.serversPath = serversPath;
        this.serverData = serverData;
        this.zkClient = new ZkClient(this.zkAddres,SESSION_TIME_OUT,CONNECTION_TIME_OUT,new SerializableSerializer());
        this.registProvider = new DefaultRegistProvider();
    }

    @Override
    public void bind() {
        if(binded){
            return ;
        }
        System.out.println(serverData.getPort() + ":binding ...");

        try {
            initRunning();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        bootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline channelPipeline = socketChannel.pipeline();
                        channelPipeline.addLast(new ServerHandler(new DefaultBalanceUpdateProvider(currentServerPath,zkClient))); // 这个管理权重
                    }
                });
        try {
            channelFuture = bootstrap.bind(serverData.getPort()).sync();
            binded = true;
            System.out.println(serverData.getPort() + ":binded ...");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }

    // 初始化服务端
    private void initRunning() throws Exception {
        String mePath = serversPath.concat("/").concat(serverData.getPort().toString());

        registProvider.regist(new ZooKeeperRegistContext(mePath,zkClient,serverData));
        currentServerPath = mePath;
    }
}
