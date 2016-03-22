package cn.las.client;

import cn.las.rtsp.DescribeRequest;
import cn.las.rtsp.OptionsRequest;
import cn.las.rtsp.PlayRequest;
import cn.las.rtsp.SetUpRequest;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspHeaderValues;
import io.netty.handler.codec.rtsp.RtspMethods;
import org.apache.commons.lang.StringUtils;

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
        System.out.println("------------------------------");
        if (rep.status().equals(HttpResponseStatus.OK)) {
            Client client = ClientManager.get(ctx.channel().id().asLongText());
            Callable<HttpRequest> request = null;
            if (client.getStatus() == null) {
                request = new OptionsRequest(client);
                client.setStatus(RtspMethods.OPTIONS);
            } else if (client.getStatus().equals(RtspMethods.OPTIONS)) {
                request = new DescribeRequest(client);
                client.setStatus(RtspMethods.DESCRIBE);
            }else if (client.getStatus().equals(RtspMethods.DESCRIBE)) {
                request = new SetUpRequest(client,1);
                client.setStatus(RtspMethods.SETUP);
            }else if (client.getStatus().equals(RtspMethods.SETUP)) {
                if (StringUtils.isEmpty(client.getSession())){
                    client.setSession(rep.headers().get(RtspHeaderNames.SESSION).toString());
                    System.out.println("session : "+client.getSession());
                    request = new SetUpRequest(client,2);
                }else{
                    request = new PlayRequest(client);
                    client.setStatus(RtspMethods.PLAY);
                }
            }else if (client.getStatus().equals(RtspMethods.PLAY)) {
//                request = new DescribeRequest(client);
            }else if (client.getStatus().equals(RtspMethods.PAUSE)) {
//                request = new DescribeRequest(client);
            }else if (client.getStatus().equals(RtspMethods.TEARDOWN)) {
//                request = new DescribeRequest(client);
            }
            if(request!=null){
                client.setCseq(client.getCseq()+1);
                HttpRequest req=request.call();
                System.out.println(req);
                ChannelFuture future=ctx.writeAndFlush(req);
                System.out.println("------------------------------");
            }
        }


    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause);
    }
}
