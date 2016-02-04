package server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.rtsp.RtspHeaders;
import io.netty.handler.codec.rtsp.RtspResponseStatuses;
import io.netty.handler.codec.rtsp.RtspVersions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/2/3
 */
public class RequestHandler extends SimpleChannelInboundHandler<HttpObject> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject httpObject) throws Exception {
        DefaultFullHttpRequest request = (DefaultFullHttpRequest) httpObject;
        System.out.println(request);
        Channel channel=ctx.channel();
        HttpResponse response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.OK);
        response.headers().add(RtspHeaders.Names.SERVER, "RtspServer");
        response.headers().add(RtspHeaders.Names.CSEQ, request.headers().get(RtspHeaders.Names.CSEQ));
        response.headers().add(RtspHeaders.Names.PUBLIC, "SETUP,PLAY,PAUSE,TEARDOWN,GET_PARAMETER,OPTION");
        ChannelFuture future=channel.writeAndFlush(response);
    }
}
