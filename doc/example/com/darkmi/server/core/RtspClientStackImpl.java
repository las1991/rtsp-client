package com.darkmi.server.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RtspClientStackImpl implements RtspStack {
    private static Logger logger = LoggerFactory.getLogger(RtspClientStackImpl.class);
    private String ip;
    private int port;
    private Bootstrap rtspClient;
    private RtspListener listener = null;
    private Channel channel;

    public RtspClientStackImpl(String ip,int port) {
        this.ip = ip;
        this.port = port;
    }

    public void start() {
        if(rtspClient==null){
            EventLoopGroup group = new NioEventLoopGroup();
            rtspClient = new Bootstrap();
            rtspClient.group(group).channel(NioSocketChannel.class);
            rtspClient.handler(new RtspClientInitializer(this).get());
            rtspClient.option(ChannelOption.SO_KEEPALIVE, true);
        }
        channel = connect(this.ip, this.port);
    }

    public void stop() {
        // Wait for the server to close the connection.
        try {
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error(String.format("连接Server(IP[%s],PORT[%s])失败", this.ip, this.port), e);
        }

    }

    protected void processRtspResponse(HttpResponse rtspResponse) {
        synchronized (this.listener) {
            listener.onRtspResponse(rtspResponse);
        }
    }

    protected void processRtspRequest(HttpRequest rtspRequest, Channel channel) {
        synchronized (this.listener) {
            listener.onRtspRequest(rtspRequest, channel);
        }
    }

    public void sendRequest(HttpRequest rtspRequest, String host, int port) {
        logger.debug("send Request { ...");
        if (channel != null) {
            try {
                channel.writeAndFlush(rtspRequest).sync();
            } catch (InterruptedException e) {
                logger.error("消息发送失败.", e);
            }
        } else {
            logger.warn("消息发送失败,连接尚未建立!");
        }
        logger.debug("send Request ... } ");
    }

    private Channel connect(String host, int port) {
        Channel ch = null;
        try {
            ch = rtspClient.connect(host, port).sync().channel();
        } catch (Exception e) {
            logger.error(String.format("连接Server(IP[%s],PORT[%s])失败", host, port), e);
        }
        return ch;
    }


    public String getAddress() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public void setRtspListener(RtspListener listener) {
        this.listener = listener;
    }
}
