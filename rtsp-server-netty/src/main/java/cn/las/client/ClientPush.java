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
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/24
 */
public class ClientPush extends AbstractClient {

    private long timestamp;
    private int seq;

    private final static String PROTOCOL = "TLS";
    private final static String CLIENT_KEY_STORE = "";
    private final static String CLIENT_KEY_STORE_PASSWORD = "";
    private final static String CLIENT_TRUST_KEY_STORE = ClientPush.class.getClassLoader().getResource("").getPath()+"clientTruststore.jks";
    private final static String CLIENT_TRUST_KEY_STORE_PASSWORD = "123456";

    public ClientPush(String url) {
        this.userAgent = "DarwinInjector (LIVE555 Streaming Media v2014.07.25)";
        this.cseq = 1;
        this.url = url;
        try {
            URI uri = new URI(this.url);
            this.host=uri.getHost();
            this.port=1554;
        } catch (URISyntaxException e) {
        }
    }

    @Override
    public ChannelHandler getHandler() {


        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                InputStream in;
                SSLContext clientContext;
                KeyStore tks2 = KeyStore.getInstance("JKS");
                tks2.load(new FileInputStream(CLIENT_TRUST_KEY_STORE), CLIENT_TRUST_KEY_STORE_PASSWORD.toCharArray());
                // Set up key manager factory to use our key store
                TrustManagerFactory tmf2 = TrustManagerFactory.getInstance("SunX509");
                tmf2.init(tks2);
                clientContext = SSLContext.getInstance(PROTOCOL);
                clientContext.init(null, tmf2.getTrustManagers(), null);
                SSLEngine engine = clientContext.createSSLEngine();
                engine.setUseClientMode(true);

                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("ssl", new SslHandler(engine));
                pipeline.addLast("decoder", new RtspResponseDecoder());
                pipeline.addLast("rtsp-encoder", new RtspRequestEncoder());
                pipeline.addLast("rtp-encoder", new RtpEncoder());
                pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
                pipeline.addLast("chunk", new ChunkedWriteHandler());
                pipeline.addLast("handler", new RtspClientHandler());

            }
        };
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static void main(String[] args) throws InterruptedException {
        final String host="54.222.135.41";
        for (int i=0;i<1;i++){
            final int finalI = i;
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    String url = "rtsp://"+host+":554/4FE2A3AE23D4C959419186930DC9CE98.sdp";//"+ finalI +"
                    try {
                        AbstractClient client = new ClientPush(url);
                        client.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.join();
            t.start();
        }
    }
}
