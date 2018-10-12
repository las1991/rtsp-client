package cn.las.client;

import cn.las.traffic.Traffic;
import cn.las.util.Md5Util;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author las
 * @date 18-9-29
 */
public class RtspTranslator {
    static Logger LOGGER = LoggerFactory.getLogger(RtspTranslator.class);

    private static NioEventLoopGroup GROUP;
    private static NioEventLoopGroup GROUP1;
    private static EventExecutorGroup WORK;
    private static Bootstrap BOOTSTRAP;

    static {
        /**
         * -Dio.netty.allocator.type=pooled
         * -Dio.netty.noPreferDirect=false
         * -Dio.netty.leakDetection.level=PARANOID
         */
        System.setProperty("io.netty.allocator.type", "pooled");
        System.setProperty("io.netty.noPreferDirect", "false");
        System.setProperty("io.netty.leakDetection.level", "PARANOID");
    }


    private static void init() {
        GROUP = new NioEventLoopGroup(1, new DefaultThreadFactory("client-io-work", false));
        GROUP1 = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), new DefaultThreadFactory("client1-io-work", false));
//        WORK = new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors() * 2 + 1, new DefaultThreadFactory("client-handler-work", false));
        BOOTSTRAP = new Bootstrap();
        Traffic.globalTrafficShapingHandler(GROUP);
    }

    private static void shutdown() {
        GROUP.shutdownGracefully();
        WORK.shutdownGracefully();
    }

    public static void main(String[] args) {

        if (args.length < 4) {
            LOGGER.error("usage [source url] [host] [port] [count]");
            return;
        }

        try {

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
                if (player.session().getState() == RtspState.TEARDOWN) {
                    LOGGER.error("player not start");
                    shutdown();
                }
                try {
                    Thread.sleep(1000);
                    LOGGER.info("wait player");
                } catch (InterruptedException e) {
                }
            }
            LOGGER.info("player ready!!! ");
            List<Recorder> recorders = new ArrayList<>();
            String uuid = UUID.randomUUID().toString();
            for (int i = 0; i < count; i++) {
                final String token = Md5Util.md5(i + uuid);
                String url = "rtsp://" + host + ":" + port + "/" + token;
                try {
                    LOGGER.info("start recoder {}-{}", i, token);
                    Recorder recorder = new Recorder(url, player);
                    recorder.start(GROUP1, WORK);
                    recorders.add(recorder);
                } catch (Exception e) {
                    LOGGER.error("start recoder {}-{} fail by {}", i, token, e.getMessage());
                }
            }

            //wait close
            player.channel().closeFuture().sync();
            for (Recorder recorder :
                    recorders) {
                recorder.channel().closeFuture().sync();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }

    }
}
