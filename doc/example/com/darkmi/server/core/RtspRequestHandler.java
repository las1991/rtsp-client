package com.darkmi.server.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RtspRequestHandler extends SimpleChannelInboundHandler<DefaultFullHttpRequest> {
    private static Logger logger = LoggerFactory.getLogger(RtspRequestHandler.class);
    private final RtspServerStackImpl rtspServerStackImpl;

    protected RtspRequestHandler(RtspServerStackImpl rtspServerStackImpl) {
        this.rtspServerStackImpl = rtspServerStackImpl;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(), cause);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DefaultFullHttpRequest request) throws Exception {
        logger.debug("client request ========>\n{}\n\n{}", request.toString(), request.content()
                .toString(CharsetUtil.UTF_8));
        rtspServerStackImpl.processRtspRequest(request, ctx.channel());
    }
}
