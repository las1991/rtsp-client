package cn.las.client;

import cn.las.client.Handler.RtspClientHandler;
import cn.las.encoder.RtpEncoder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.rtsp.RtspRequestEncoder;
import io.netty.handler.codec.rtsp.RtspResponseDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/24
 */
public class ClientPush extends AbstractClient {

    public ClientPush(String url) {
        this.userAgent = "LibVLC/2.2.2 (LIVE555 Streaming Media v2016.01.12)";
        this.cseq = 1;
        this.url = url;
    }

    @Override
    public ChannelHandler getHandler() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("decoder", new RtspResponseDecoder());
                pipeline.addLast("rtsp-encoder", new RtspRequestEncoder());
                pipeline.addLast("rtp-encoder", new RtpEncoder());
                pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
                pipeline.addLast("chunk", new ChunkedWriteHandler());
                pipeline.addLast("handler", new RtspClientHandler());
            }
        };
    }
}
