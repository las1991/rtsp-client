package client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/2/4
 */
public class ResponseHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, FullHttpResponse  fullHttpResponse) throws Exception {
        System.out.println("rep :"+fullHttpResponse.toString());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }
}
