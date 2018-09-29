package cn.las.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/24
 */
public interface Client {
    Client start(Bootstrap bootstrap);

    ChannelHandler getHandler(EventExecutorGroup work);
}
