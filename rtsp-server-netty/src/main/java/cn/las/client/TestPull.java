package cn.las.client;

import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Created by las on 2017/4/10.
 */
public class TestPull {


    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            String url = "rtsp://52.81.104.103:554/15B20416DDEEE1BC5AFE7564E75D4A00";
            Player player = new Player(url);
            PlayerLogger playerLogger = new PlayerLogger(player);
            player.start(group, null);
        } finally {

        }
    }
}
