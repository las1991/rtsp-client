package cn.las.client;

import cn.las.util.Md5Util;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * @author las
 * @date 18-9-29
 */
public class RtspTranslator {
    static Logger LOGGER = LoggerFactory.getLogger(RtspTranslator.class);

    private static NioEventLoopGroup GROUP;
    private static EventExecutorGroup WORK;
    private static Bootstrap BOOTSTRAP;


    private static void init() {
        GROUP = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2 + 1, new DefaultThreadFactory("client-io-work", false));
        WORK = new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors() * 2 + 1, new DefaultThreadFactory("client-handler-work", false));
        BOOTSTRAP = new Bootstrap();
    }

    public static void main(String[] args) {
        if (args.length < 4) {
            LOGGER.error("usage [source url] [host] [port] [count]");
            return;
        }

        String source = StringUtils.isEmpty(args[0]) ? "rtsp://10.100.102.29:554/aaa" : args[0];
        String host = StringUtils.isEmpty(args[1]) ? "127.0.0.1" : args[1];
        int port = 554;
        try {
            port = Integer.parseInt(args[2]);
        } catch (Exception e) {
        }

        int count = 0;
        try {
            count = Integer.parseInt(args[3]);
        } catch (Exception e) {
        }

        init();

        Player player = new Player(source);
        player.start(GROUP, WORK);

        while (player.session().getState() != RtspState.PLAY) {
            try {
                Thread.sleep(1000);
                LOGGER.info("wait player");
            } catch (InterruptedException e) {
            }
        }
        LOGGER.info("player ready!!! ");
        String uuid = UUID.randomUUID().toString();
        for (int i = 0; i < count; i++) {
            final String token = Md5Util.md5(i + uuid);
            String url = "rtsp://" + host + ":" + port + "/" + token;
            try {
                LOGGER.info("start recoder {}-{}", i, token);
                new Recorder(url, player).start(GROUP, WORK);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
