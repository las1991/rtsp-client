package cn.las.client;

import cn.las.client.handler.RtpHandler;
import cn.las.client.handler.RtspClientHandler;
import cn.las.decoder.RtpOverTcpDecoder;
import cn.las.rtp.FramingRtpPacket;
import cn.las.rtsp.OptionsRequest;
import cn.las.ssl.SSL;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.rtsp.RtspDecoder;
import io.netty.handler.codec.rtsp.RtspEncoder;
import io.netty.util.concurrent.EventExecutorGroup;

import java.io.IOException;
import java.util.Observable;

/**
 * @author las
 * @date 18-9-29
 */
public class Player extends Observable implements Client {

    private final RtspSession session;

    private Channel channel;

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

        future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE).addListener(new ChannelFutureListener() {
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
    public void close() throws IOException {
        channel.close();
    }

    public void onFramingRtpPacket(FramingRtpPacket rtpPacket) {
        try {
            this.setChanged();
            this.notifyObservers(rtpPacket.retain());
        } finally {
            rtpPacket.retain();
        }
    }
}
