package com.darkmi.server.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RtspResponseHandler extends SimpleChannelInboundHandler<DefaultFullHttpResponse> {
    private static Logger logger = LoggerFactory.getLogger(RtspResponseHandler.class);
    private final RtspClientStackImpl rtspClientStackImpl;

    public RtspResponseHandler(RtspClientStackImpl rtspClientStackImpl) {
        this.rtspClientStackImpl = rtspClientStackImpl;
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, DefaultFullHttpResponse msg) throws Exception {
        logger.debug(msg.toString());
        rtspClientStackImpl.processRtspResponse(msg);
    }
}
