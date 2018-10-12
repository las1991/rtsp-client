package cn.las.ssl;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslProvider;

import java.util.List;

/**
 * @author las
 * @date 18-9-29
 */
public class SSL {

    static List<String> ciphers = Lists.newArrayList(

//            "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
//            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
            // GCM (Galois/Counter Mode) requires JDK 8.
            "TLS_RSA_WITH_AES_128_GCM_SHA256"
//            "TLS_RSA_WITH_AES_128_CBC_SHA256"
    );

    static SslContext sslContext;

    static {
        try {
            sslContext = SslContextBuilder.forClient()
                    .sslProvider(SslProvider.OPENSSL)
                    .ciphers(ciphers)
                    .build();
        } catch (Exception e) {

        }
    }

    public static ChannelHandler getSslHandler(ByteBufAllocator alloc) {
        return new SslHandler(sslContext.newEngine(alloc));
    }


}
