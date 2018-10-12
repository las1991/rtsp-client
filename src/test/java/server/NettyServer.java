package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.rtsp.RtspRequestDecoder;
import io.netty.handler.codec.rtsp.RtspResponseEncoder;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/2/3
 */
public class NettyServer {

    private static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final int BIZTHREADSIZE = 4;
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZGROUPSIZE);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(BIZTHREADSIZE);

    public void start(String ip, Integer port) throws InterruptedException {
        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workerGroup);
            server.channel(NioServerSocketChannel.class);
            server.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("decoder", new RtspRequestDecoder());
                    pipeline.addLast("encoder", new RtspResponseEncoder());
                    pipeline.addLast("aggregator", new HttpObjectAggregator(1024 * 1024 * 64));
                    pipeline.addLast("handler", new RequestHandler());
                }
            });
            ChannelFuture f = server.bind(ip, port).sync();
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        NettyServer server = new NettyServer();
        server.start("127.0.0.1", 554);
    }
}
