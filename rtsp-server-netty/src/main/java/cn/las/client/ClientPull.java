package cn.las.client;

import cn.las.client.Handler.RtspClientHandler;
import cn.las.decoder.RtpDecoder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.rtsp.RtspRequestEncoder;
import io.netty.handler.codec.rtsp.RtspResponseDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/22
 */
public class ClientPull extends AbstractClient {

    public ClientPull(String url) {
        this.userAgent="LibVLC/2.2.2 (LIVE555 Streaming Media v2016.01.12)";
        this.cseq=1;
        this.url = url;
        try {
            URI uri = new URI(this.url);
            this.host=uri.getHost();
            this.port=uri.getPort();
        } catch (URISyntaxException e) {
        }
    }

    @Override
    public ChannelHandler getHandler() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("rtp-decoder", new RtpDecoder());
                pipeline.addLast("rtsp-decoder", new RtspResponseDecoder());
                pipeline.addLast("encoder", new RtspRequestEncoder());
                pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
                pipeline.addLast("chunk", new ChunkedWriteHandler());
                pipeline.addLast("handler", new RtspClientHandler());
            }
        };
    }

    public static void main(String[] args) {
        String url = "rtsp://54.223.242.201:554/7947B6B48864E301AC3064E426F33403.sdp";
        try {
            AbstractClient client = new ClientPull(url);
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
