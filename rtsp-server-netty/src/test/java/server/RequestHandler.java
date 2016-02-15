package server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
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
public class RequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());




    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, FullHttpRequest httpObject) throws Exception {
        System.out.println("messageReceived :["+httpObject.toString()+"]");
        FullHttpRequest request =  httpObject;
        Channel channel = channelHandlerContext.channel();
        HttpResponse response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.OK);
        response.headers().add(RtspHeaderNames.SERVER, "RtspServer");
        response.headers().add(RtspHeaderNames.CSEQ, request.headers().get(RtspHeaderNames.CSEQ));
        response.headers().add(RtspHeaderNames.PUBLIC, "SETUP,PLAY,PAUSE,TEARDOWN,GET_PARAMETER,OPTION");
        ChannelFuture future = channel.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        try {
            throw  cause;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
