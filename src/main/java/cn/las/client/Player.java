package cn.las.client;

import cn.las.client.handler.RtpHandler;
import cn.las.client.handler.RtspClientHandler;
import cn.las.decoder.RtpOverTcpDecoder;
import cn.las.observer.AbstractObservable;
import cn.las.rtp.FramingRtpPacket;
import cn.las.rtsp.OptionsRequest;
import cn.las.ssl.SSL;
import cn.las.traffic.Traffic;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.rtsp.RtspDecoder;
import io.netty.handler.codec.rtsp.RtspEncoder;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author las
 * @date 18-9-29
 */
public class Player extends AbstractObservable implements Client {

    Logger logger = LoggerFactory.getLogger(getClass());

    private final RtspSession session;

    private Channel channel;

    private long lastLogTime;

    public Player(String url) {
        this.session = new RtspSession(url);
    }

    @Override
    public Client start(NioEventLoopGroup group, EventExecutorGroup workGroup) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class).handler(getHandler(workGroup));
        ChannelFuture future = bootstrap.connect(session.host, session.port);
        this.channel = future.channel();
        if (session.port == 1554) {
            channel.pipeline().addFirst("ssl", SSL.getSslHandler(channel.alloc()));
        }

        future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
                .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            doOption();
                        }
                    }
                });
        return this;
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public ChannelHandler getHandler(EventExecutorGroup work) {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(Traffic.globalTrafficShapingHandler(work));
                pipeline.addLast("rtpOverTcpDecoder", new RtpOverTcpDecoder());
                pipeline.addLast("rtpHandler", new RtpHandler(Player.this));

                pipeline.addLast("rtspDecoder", new RtspDecoder());
                pipeline.addLast("rtspEncoder", new RtspEncoder());
                pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
                pipeline.addLast(work, "handler", new RtspClientHandler(Player.this));
            }
        };
    }


    @Override
    public RtspSession session() {
        return this.session;
    }

    @Override
    public void doOption() {
        OptionsRequest request = new OptionsRequest(this.session);
        HttpRequest req = request.call();
        channel().writeAndFlush(req);
    }

    public void doPlay() {
    }

    @Override
    public void close() {
        channel.close();
    }

    public void onFramingRtpPacket(FramingRtpPacket rtpPacket) {
        long start = System.currentTimeMillis();
        try {
            this.notifyObservers(rtpPacket.duplicate().retain());
        } finally {
            rtpPacket.release();
            if (System.currentTimeMillis() - lastLogTime >= 5000) {
                logger.info("rtp {} cost {}ms", rtpPacket.getLength(), System.currentTimeMillis() - start);
                lastLogTime = System.currentTimeMillis();
            }
        }

    }
}
