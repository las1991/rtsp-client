package cn.las.client;

import cn.las.rtsp.DescribeRequest;
import cn.las.rtsp.OptionsRequest;
import cn.las.rtsp.SetUpRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.rtsp.RtspMethods;

import java.util.concurrent.Callable;


/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/22
 */
public class RtspClientHandler extends SimpleChannelInboundHandler<HttpResponse> {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpResponse rep) throws Exception {
        System.out.println(rep);
        if (rep.status().equals(HttpResponseStatus.OK)) {
            Client client = ClientManager.get(ctx.channel().id().asLongText());
            Callable<HttpRequest> request = null;
            if (client.getStatus() == null) {
                request = new OptionsRequest(client);
            } else if (client.getStatus().equals(RtspMethods.OPTIONS)) {
                request = new DescribeRequest(client);
            }else if (client.getStatus().equals(RtspMethods.DESCRIBE)) {
                request = new SetUpRequest(client);
            }else if (client.getStatus().equals(RtspMethods.SETUP)) {
//                request = new DescribeRequest(client);
            }else if (client.getStatus().equals(RtspMethods.PLAY)) {
//                request = new DescribeRequest(client);
            }else if (client.getStatus().equals(RtspMethods.PAUSE)) {
//                request = new DescribeRequest(client);
            }else if (client.getStatus().equals(RtspMethods.TEARDOWN)) {
//                request = new DescribeRequest(client);
            }
            HttpRequest req=request.call();
            System.out.println(req);
            ctx.writeAndFlush(req);
        }


    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause);
    }
}
