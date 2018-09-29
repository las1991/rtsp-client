package cn.las.client;

import cn.las.rtsp.OptionsRequest;
import cn.las.stream.VideoStream;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import org.mp4parser.muxer.Sample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xpertss.sdp.SessionDescription;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/24
 */
public abstract class AbstractClient implements Client {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private EventLoopGroup group;
    private EventExecutorGroup work;
    private Bootstrap bootstrap;

    public AbstractClient() {
        group = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2 + 1, new DefaultThreadFactory("client-io-work", false));
        work = new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors() * 4 + 1, new DefaultThreadFactory("client-handler-work", false));
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(this.getHandler(work)).option(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public void start(final String url) throws Exception {
        if (this instanceof ClientPull) {
            type = ClientSession.Type.Pull;
        } else if (this instanceof ClientPush) {
            type = ClientSession.Type.Push;
        }
        final ClientSession clientStatus = new ClientSession(url, type);
        try {
            clientStatus.future = bootstrap.connect(clientStatus.host, clientStatus.port);
            clientStatus.future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        ClientManager.put(future.channel().id().asLongText(), clientStatus);
                        logger.info("client start at " + future.channel().localAddress() + " ,url : " + url);
                        OptionsRequest request = new OptionsRequest(clientStatus);
                        clientStatus.status = RtspMethods.OPTIONS;
                        HttpRequest req = request.call();
                        future.channel().writeAndFlush(req);
                    } else {
                        future.channel().close();
                    }
                }
            });
        } finally {
//            group.shutdownGracefully();
        }
    }

}



