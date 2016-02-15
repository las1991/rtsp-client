package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.rtsp.*;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/2/3
 */
public class RtspClient {

    private static final Logger logger = Logger.getLogger(RtspClient.class);

    private final String host;
    private final int port;
    private final InetAddress inetAddress;

    private Channel channel;
    private Bootstrap bootstrap;

    public RtspClient(String host, Integer port) throws UnknownHostException {
        this.host = host;
        this.port = port;
        this.inetAddress = InetAddress.getByName(this.host);
    }

    public void start() throws InterruptedException, URISyntaxException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("decoder", new RtspResponseDecoder());
                    pipeline.addLast("encoder", new RtspRequestEncoder());
                    pipeline.addLast("aggregator", new HttpObjectAggregator(1024 * 1024 * 64));
                    pipeline.addLast("handler", new ResponseHandler());

                }
            });
            b.option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFutrue = b.connect(host, port).sync();
            if (channelFutrue.isSuccess()) {
                channel = channelFutrue.channel();
                URI uri = new URI("rtsp://127.0.0.1:554/hello-world");
                HttpRequest request = this.buildOptionsRequest(uri.toASCIIString());
                this.send(request);
                channel.closeFuture().sync();
            }
        } finally {
            // 优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }
    }

    public void send(HttpRequest request) throws InterruptedException {
        if (channel != null) {
            ChannelFuture future = channel.writeAndFlush(request).sync();
        } else {
            System.out.println("消息发送失败,连接尚未建立!");
        }
    }


    public static void main(String[] args) throws Exception {
        RtspClient client = new RtspClient("127.0.0.1", 554);
        client.start();
    }


    /**
     * build DESCRIBE request
     *
     * @return
     */
    private HttpRequest buildDescribeRequest(String uri) {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.DESCRIBE, uri);
        request.headers().set(RtspHeaderNames.CSEQ, "1");
        request.headers().add(RtspHeaderNames.ACCEPT, "application/sdp, application/rtsl, application/mheg");
        return request;
    }

    private HttpRequest buildOptionsRequest(String uri) {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.OPTIONS, uri);
        request.headers().set(RtspHeaderNames.CSEQ, "1");
        request.headers().set(RtspHeaderNames.REQUIRE, "implicit-play");
        request.headers().set(RtspHeaderNames.PROXY_REQUIRE, "gzipped-messages");
        return request;
    }

    private HttpRequest buildAnnounceRequest(String uri) {
        HttpRequest request = new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.ANNOUNCE, uri);
        request.headers().set(RtspHeaderNames.CSEQ, "1");
        request.headers().set(RtspHeaderNames.SESSION, "1234567");
        request.headers().add(RtspHeaderNames.CONTENT_TYPE, "application/sdp");
        request.headers().add(RtspHeaderNames.CONTENT_LENGTH, "123");
//        request.setContent(null);
        return request;
    }

    /**
     * build SETUP request
     *
     * @return
     */
    private HttpRequest buildSetupRequest(String uri) {
        HttpRequest request = new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.SETUP,
                uri);
        request.headers().set(RtspHeaderNames.CSEQ, "3");
        request.headers().add(RtspHeaderNames.TRANSPORT, "RTP/AVP;unicast;client_port=4588-4589");

        return request;
    }

    /**
     * build PLAY request
     *
     * @return
     */
    private HttpRequest buildPlayRequest(String uri) {
        HttpRequest request = new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.PLAY, uri);
        request.headers().set(RtspHeaderNames.CSEQ, "4");
        request.headers().set(RtspHeaderNames.SESSION, "1234567");
        request.headers().add(RtspHeaderNames.RANGE, "npt=10-15");

        return request;
    }

    /**
     * build PAUSE request
     *
     * @return
     */
    private HttpRequest buildPauseRequest(String uri) {
        HttpRequest request = new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.PAUSE, uri);
        request.headers().set(RtspHeaderNames.CSEQ, "5");
        request.headers().set(RtspHeaderNames.SESSION, "1234567");

        return request;
    }

    /**
     * build TEARDOWN request
     *
     * @return
     */
    private HttpRequest buildTeardownRequest(String uri) {
        HttpRequest request = new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.TEARDOWN, uri);
        request.headers().set(RtspHeaderNames.CSEQ, "6");
        request.headers().set(RtspHeaderNames.SESSION, "1234567");
        return request;
    }

}
