package com.darkmi.server.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RtspResponseHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(RtspResponseHandler.class);
    private final RtspClientStackImpl rtspClientStackImpl;

    public RtspResponseHandler(RtspClientStackImpl rtspClientStackImpl) {
        this.rtspClientStackImpl = rtspClientStackImpl;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof DefaultFullHttpResponse) {
            DefaultFullHttpResponse response = (DefaultFullHttpResponse) msg;
            logger.debug(response.toString());
            rtspClientStackImpl.processRtspResponse(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
