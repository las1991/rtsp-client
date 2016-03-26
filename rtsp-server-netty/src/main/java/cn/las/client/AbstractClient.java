package cn.las.client;

import cn.las.rtsp.OptionsRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.rtsp.RtspMethods;

import java.net.URI;
import java.util.Properties;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/24
 */
public abstract class AbstractClient implements Client{

    protected String host;
    protected Integer port;
    protected Channel channel;
    protected ChannelFuture future;
    protected String url;
    protected HttpMethod status;
    protected String session;
    protected String userAgent;
    protected Properties sdp = new Properties();
    protected Integer cseq;

    @Override
    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(this.getHandler()).option(ChannelOption.SO_KEEPALIVE, true);
            this.future = bootstrap.connect(this.host, this.port).sync();
            this.channel = future.channel();
            ClientManager.put(this.channel.id().asLongText(), this);
            OptionsRequest request = new OptionsRequest(this);
            this.status= RtspMethods.OPTIONS;
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

    public Properties getSdp() {
        return sdp;
    }

    public void setSdp(Properties sdp) {
        this.sdp = sdp;
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
}
