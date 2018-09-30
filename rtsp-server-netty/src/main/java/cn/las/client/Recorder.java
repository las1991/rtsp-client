package cn.las.client;

import cn.las.client.handler.RtspClientHandler;
import cn.las.encoder.RtpOverTcpEncoder;
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

import java.util.Observable;
import java.util.Observer;

/**
 * @author las
 * @date 18-9-29
 */
public class Recorder implements Client, Observer {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final RtspSession session;
    private final Player player;
    private Channel channel;

    public Recorder(String url, Player player) {
        session = new RtspSession(url);
        this.player = player;
    }

    @Override
    public Client start(NioEventLoopGroup group, EventExecutorGroup workGroup) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class).handler(getHandler(workGroup));

        bootstrap.option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 10 * 64 * 1024);
        bootstrap.option(ChannelOption.SO_SNDBUF, 1048576);
        bootstrap.option(ChannelOption.SO_RCVBUF, 1048576);
//        bootstrap.option(ChannelOption.TCP_NODELAY, true);

        ChannelFuture future = bootstrap.connect(session.host, session.port);
        this.channel = future.channel();
        if (session.port == 1554 || session.port == 1443) {
            channel.pipeline().addFirst("ssl", SSL.getSslHandler(channel.alloc()));
        }
        future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
                .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            doOption();
                        } else {

                        }
                    }
                });

        return this;
    }

    @Override
    public Channel channel() {
        return this.channel;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public ChannelHandler getHandler(EventExecutorGroup work) {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(Traffic.globalTrafficShapingHandler(work));
//                pipeline.addLast("idle", new IdleStateHandler(0, 50, 0, TimeUnit.MILLISECONDS));
                pipeline.addLast("rtpOverTcpEncoder", new RtpOverTcpEncoder());
                pipeline.addLast("rtspDecoder", new RtspDecoder());
                pipeline.addLast("rtspEncoder", new RtspEncoder());
                pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
                pipeline.addLast(work, "handler", new RtspClientHandler(Recorder.this));

            }
        };
    }

    @Override
    public RtspSession session() {
        return this.session;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Player && arg instanceof FramingRtpPacket) {
            FramingRtpPacket packet = (FramingRtpPacket) arg;
            onRtp(packet);
        }
    }

    private void onRtp(FramingRtpPacket packet) {
        if (writeable()) {
            channel.writeAndFlush(packet.duplicate().retain());
        } else {
//            channel.flush();
//            channel.writeAndFlush(packet.duplicate().retain());
//            logger.error("{} can't write", session.getToken());
        }
    }

    private boolean writeable() {
        return null != channel && channel.isWritable();
    }

    @Override
    public void doOption() {
        OptionsRequest request = new OptionsRequest(this.session);
        HttpRequest req = request.call();
        channel().writeAndFlush(req);
    }

    public void doRecoder() {
        logger.info("{} started", session.getToken());
        player.addObserver(this);
    }

    @Override
    public void close() {
        if (channel.isOpen()) {
            logger.info("close {}-{}", session.getToken(), channel);
            channel.close();
        }
    }
}
