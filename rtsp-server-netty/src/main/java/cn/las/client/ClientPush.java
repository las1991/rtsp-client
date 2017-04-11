package cn.las.client;

import cn.las.client.Handler.RtspClientHandler;
import cn.las.encoder.RtpEncoder;
import cn.las.util.Md5Util;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.rtsp.RtspRequestEncoder;
import io.netty.handler.codec.rtsp.RtspResponseDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/24
 */
public class ClientPush extends AbstractClient {

    private final static String PROTOCOL = "TLS";
    private final static String CLIENT_KEY_STORE = "";
    private final static String CLIENT_KEY_STORE_PASSWORD = "";
    private final static String CLIENT_TRUST_KEY_STORE = "/clientTruststore.jks";
    private final static String CLIENT_TRUST_KEY_STORE_PASSWORD = "123456";

    public ClientPush() {
        super();
    }

    @Override
    public ChannelHandler getHandler() {


        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                /*InputStream in;
                SSLContext clientContext;
                KeyStore tks2 = KeyStore.getInstance("JKS");
                tks2.load(new FileInputStream(CLIENT_TRUST_KEY_STORE), CLIENT_TRUST_KEY_STORE_PASSWORD.toCharArray());
                // Set up key manager factory to use our key store
                TrustManagerFactory tmf2 = TrustManagerFactory.getInstance("SunX509");
                tmf2.init(tks2);
                clientContext = SSLContext.getInstance(PROTOCOL);
                clientContext.init(null, tmf2.getTrustManagers(), null);
                SSLEngine engine = clientContext.createSSLEngine();
                engine.setUseClientMode(true);*/

                ChannelPipeline pipeline = ch.pipeline();
//                pipeline.addLast("ssl", new SslHandler(engine));
                pipeline.addLast("idle", new IdleStateHandler(0, 10, 100, TimeUnit.MILLISECONDS));
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
