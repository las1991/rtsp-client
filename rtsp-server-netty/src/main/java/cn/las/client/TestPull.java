package cn.las.client;

import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Created by las on 2017/4/10.
 */
public class TestPull {


    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();

        /**
         * 50KB/s rtsp://52.80.188.46:554/7260CF92C4CAC564639758CDA269E212
         * 150KB/s rtsp://52.81.104.103:554/15B20416DDEEE1BC5AFE7564E75D4A00
         * 150KB/s rtsp://52.81.104.103:554/5511B7ADDCBB5C7FF62AFBA84D727F40
         */
        try {
            String url = "rtsp://52.82.59.252:554/aaa";
            Player player = new Player(url);
            PlayerLogger playerLogger = new PlayerLogger(player);
            player.start(group, group);

            player.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

    }
}
