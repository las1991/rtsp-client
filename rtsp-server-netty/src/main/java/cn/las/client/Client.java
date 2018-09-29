package cn.las.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.io.Closeable;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/24
 */
public interface Client extends Closeable {
    Client start(NioEventLoopGroup group, EventExecutorGroup workGroup);

    Channel channel();

    ChannelHandler getHandler(EventExecutorGroup work);

    RtspSession session();

    void doOption();
}
