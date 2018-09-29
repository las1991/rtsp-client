package cn.las.client;

import cn.las.client.handler.RtspClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.rtsp.RtspDecoder;
import io.netty.handler.codec.rtsp.RtspEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

/**
 * @author las
 * @date 18-9-29
 */
public class Recorder implements Client, Observer {

    private final RtspSession session;
    private final Player player;
    private Channel channel;

    public Recorder(String url, Player player) {
        session = new RtspSession(url);
        this.player = player;
        player.addObserver(this);
    }

    @Override
    public Client start(Bootstrap bootstrap) {
        ChannelFuture future = bootstrap.connect(session.host, session.port);
        future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        this.channel = future.channel();
        return this;
    }

    @Override
    public ChannelHandler getHandler(EventExecutorGroup work) {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("idle", new IdleStateHandler(0, 50, 0, TimeUnit.MILLISECONDS));
                pipeline.addLast("decoder", new RtspDecoder());
                pipeline.addLast("rtsp-encoder", new RtspEncoder());
                pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
                pipeline.addLast(work, "handler", new RtspClientHandler());

            }
        };
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Player) {
            onRtp(arg);
        }
    }

    private void onRtp(Object arg) {
        if (writeable()) {
            channel.eventLoop().submit(new Runnable() {
                @Override
                public void run() {
                    channel.writeAndFlush(arg);
                }
            });
        }
    }

    private boolean writeable() {
        return null != channel && channel.isWritable();
    }
}
