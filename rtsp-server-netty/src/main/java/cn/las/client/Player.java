package cn.las.client;

import cn.las.client.handler.RtspClientHandler;
import cn.las.decoder.RtpDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.rtsp.RtspDecoder;
import io.netty.handler.codec.rtsp.RtspEncoder;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.Observable;

/**
 * @author las
 * @date 18-9-29
 */
public class Player extends Observable implements Client {

    private final RtspSession session;

    public Player(String url) {
        this.session = new RtspSession(url);
    }

    @Override
    public Client start(Bootstrap bootstrap) {

        return this;
    }

    @Override
    public ChannelHandler getHandler(EventExecutorGroup work) {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
//                pipeline.addLast("ssl", new SslHandler(sslContext.newEngine(ch.alloc())));
                pipeline.addLast("rtp-decoder", new RtpDecoder());
                pipeline.addLast("rtsp-decoder", new RtspDecoder());
                pipeline.addLast("encoder", new RtspEncoder());
                pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
                pipeline.addLast(work, "handler", new RtspClientHandler());
            }
        };
    }

}
