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
import io.netty.util.concurrent.DefaultThreadFactory;
import org.apache.log4j.Logger;
import org.mp4parser.muxer.Sample;
import xpertss.sdp.SessionDescription;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/24
 */
public abstract class AbstractClient implements Client {

    Logger logger = Logger.getLogger(this.getClass());

    private EventLoopGroup group;
    private Bootstrap bootstrap;

    public AbstractClient() {
        DefaultThreadFactory factory = new DefaultThreadFactory("client-work", false);
        group = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2 + 1, factory);
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(this.getHandler()).option(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public void start(final String url) throws Exception {
        ClientSession.Type type = null;
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

    public static class ClientSession {

        Logger logger = Logger.getLogger(this.getClass());

        public enum Type {
            Push, Pull;
        }

        private Type type;

        protected String host;
        protected Integer port;
        protected ChannelFuture future;
        protected String url;
        protected HttpMethod status;
        protected String session;
        protected Integer cseq;
        protected int seq;
        protected long timestamp;
        protected String userAgent;
        static protected SessionDescription sdp;
        private int streams = 0;

        private long lastTimeStamp = 0;

        public ClientSession(String url, Type type) {
            this.type = type;
            this.url = url;
            try {
                URI uri = new URI(this.url);
                this.host = uri.getHost();
                this.port = uri.getPort();
            } catch (URISyntaxException e) {
            }
            this.userAgent = "DarwinInjector (LIVE555 Streaming Media v2014.07.25)";
            this.cseq = 1;
        }

        private int sampleIndex = 0;

        public Sample getVideoSample(VideoStream stream) {

            if (sampleIndex >= stream.getSamples().size()) {
                sampleIndex = 0;
            }
            if (sampleIndex < stream.getSamples().size()) {
                timestamp = sampleIndex * (1000 / stream.getFrame_rate()) * 90;
                return stream.getSamples().get(sampleIndex++);
            }
            return null;
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

        public String getSession() {
            return session;
        }

        public void setSession(String session) {
            this.session = session;
        }

        public Integer getCseq() {
            return cseq;
        }

        public void setCseq(Integer cseq) {
            this.cseq = cseq;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public SessionDescription getSdp() {
            return sdp;
        }

        public void setSdp(SessionDescription sdp) {
            this.sdp = sdp;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public int getSeq() {
            return seq;
        }

        public void setSeq(int seq) {
            this.seq = seq;
        }

        public int getStreams() {
            return streams;
        }

        public void setStreams(int streams) {
            this.streams = streams;
        }
    }
}



