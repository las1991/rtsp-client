package cn.las.client;

import cn.las.client.Handler.RtspClientHandler;
import cn.las.encoder.RtpEncoder;
import cn.las.util.Md5Util;
import com.google.common.collect.Lists;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.rtsp.RtspRequestEncoder;
import io.netty.handler.codec.rtsp.RtspResponseDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/24
 */
public class ClientPush extends AbstractClient {

    List<String> ciphers = Lists.newArrayList(

//            "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
//            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
            // GCM (Galois/Counter Mode) requires JDK 8.
//            "TLS_RSA_WITH_AES_128_GCM_SHA256"
            "TLS_RSA_WITH_AES_128_CBC_SHA256"
    );

    SslContext sslContext = SslContextBuilder.forClient()
            .sslProvider(SslProvider.OPENSSL)
            .ciphers(ciphers)
            .build();

    public ClientPush() throws SSLException {
        super();
    }

    @Override
    public ChannelHandler getHandler() {


        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {

                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("ssl", new SslHandler(sslContext.newEngine(ch.alloc())));
                pipeline.addLast("idle", new IdleStateHandler(0, 50, 0, TimeUnit.MILLISECONDS));
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
