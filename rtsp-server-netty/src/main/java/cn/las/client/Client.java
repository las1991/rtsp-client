package cn.las.client;

import io.netty.channel.ChannelHandler;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/24
 */
public interface Client {
    public void start(String url) throws Exception;

    public ChannelHandler getHandler();
}
