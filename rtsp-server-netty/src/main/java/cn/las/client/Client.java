package cn.las.client;

import cn.las.rtsp.OptionsRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.rtsp.RtspRequestEncoder;
import io.netty.handler.codec.rtsp.RtspResponseDecoder;

import java.net.SocketAddress;
import java.net.URI;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/22
 */
public class Client {

    private Channel channel;
    private ChannelFuture future;
    private String url;
    private HttpMethod status;
    private String session;
    private String userAgent = "LibVLC/2.2.2 (LIVE555 Streaming Media v2016.01.12)";

    private Integer cseq = 1;

    public Client(String url) {
        this.url = url;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            URI uri = new URI(this.url);
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("decoder", new RtspResponseDecoder());
                            pipeline.addLast("encoder", new RtspRequestEncoder());
                            pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
                            pipeline.addLast("handler", new RtspClientHandler());
                        }
                    }).option(ChannelOption.SO_KEEPALIVE, true);

            this.future = bootstrap.connect(uri.getHost(), uri.getPort()).sync();
            this.channel = future.channel();
            ClientManager.put(this.channel.id().asLongText(), this);
            OptionsRequest request = new OptionsRequest(this);
            this.channel.writeAndFlush(request.call());
            this.channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public ChannelFuture getFuture() {
        return future;
    }

    public void setFuture(ChannelFuture future) {
        this.future = future;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpMethod getStatus() {
        return status;
    }

    public void setStatus(HttpMethod status) {
        this.status = status;
    }

    public Integer getCseq() {
        return cseq;
    }

    public void setCseq(Integer cseq) {
        this.cseq = cseq;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

}
