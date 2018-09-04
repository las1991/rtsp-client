package cn.las.client;

import cn.las.client.Handler.RtspClientHandler;
import cn.las.decoder.RtpDecoder;
import com.google.common.collect.Lists;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.rtsp.RtspDecoder;
import io.netty.handler.codec.rtsp.RtspEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.net.ssl.SSLException;
import java.util.List;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/22
 */
public class ClientPull extends AbstractClient {

    List<String> ciphers = Lists.newArrayList(

//            "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
//            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
            // GCM (Galois/Counter Mode) requires JDK 8.
            "TLS_RSA_WITH_AES_128_GCM_SHA256"
//            "TLS_RSA_WITH_AES_128_CBC_SHA256"
    );

    SslContext sslContext = SslContextBuilder.forClient()
            .sslProvider(SslProvider.OPENSSL)
            .ciphers(ciphers)
            .build();

    public ClientPull() throws SSLException {

    }

    @Override
    public ChannelHandler getHandler() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("ssl", new SslHandler(sslContext.newEngine(ch.alloc())));
                pipeline.addLast("rtp-decoder", new RtpDecoder());
                pipeline.addLast("rtsp-decoder", new RtspDecoder());
                pipeline.addLast("encoder", new RtspEncoder());
                pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
                pipeline.addLast("chunk", new ChunkedWriteHandler());
                pipeline.addLast("handler", new RtspClientHandler());
            }
        };
    }

}
