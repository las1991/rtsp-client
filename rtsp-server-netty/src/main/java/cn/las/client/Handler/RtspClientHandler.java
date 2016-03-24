package cn.las.client.Handler;

import cn.las.client.AbstractClient;
import cn.las.client.Client;
import cn.las.client.ClientManager;
import cn.las.client.ClientPush;
import cn.las.mp4parser.H264Sample;
import cn.las.rtsp.DescribeRequest;
import cn.las.rtsp.OptionsRequest;
import cn.las.rtsp.PlayRequest;
import cn.las.rtsp.SetUpRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspMethods;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/22
 */
public class RtspClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private volatile ScheduledFuture<?> pushRtp;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        AbstractClient client = ClientManager.get(ctx.channel().id().asLongText());
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpResponse rep) throws Exception {
        if (rep.status().equals(HttpResponseStatus.OK)) {
            AbstractClient client = ClientManager.get(ctx.channel().id().asLongText());
            Callable<HttpRequest> request = null;
            if (client.getStatus() == null) {
                request = new OptionsRequest(client);
                client.setStatus(RtspMethods.OPTIONS);

            } else if (client.getStatus().equals(RtspMethods.OPTIONS)) {
                request = new DescribeRequest(client);
                client.setStatus(RtspMethods.DESCRIBE);

            } else if (client.getStatus().equals(RtspMethods.DESCRIBE)) {
                ByteBuf buf = rep.content();
                client.getSdp().load(new ByteBufInputStream(buf));
                request = new SetUpRequest(client, 1);
                client.setStatus(RtspMethods.SETUP);

            } else if (client.getStatus().equals(RtspMethods.SETUP)) {
                if (StringUtils.isEmpty(client.getSession())) {
                    client.setSession(rep.headers().get(RtspHeaderNames.SESSION).toString());
                    System.out.println("session : " + client.getSession());
                    request = new SetUpRequest(client, 2);
                } else {
                    request = new PlayRequest(client);
                    client.setStatus(RtspMethods.PLAY);
                }

            } else if (client.getStatus().equals(RtspMethods.PLAY)) {
                if(client instanceof ClientPush){
                    pushRtp = ctx.executor().scheduleAtFixedRate(
                            new RtspClientHandler.PushRtpTask(ctx), 0, 40,
                            TimeUnit.MILLISECONDS);
                }
//                request = new DescribeRequest(client);

            } else if (client.getStatus().equals(RtspMethods.PAUSE)) {
//                request = new DescribeRequest(client);

            } else if (client.getStatus().equals(RtspMethods.TEARDOWN)) {
//                request = new DescribeRequest(client);

            }
            if (request != null) {
                client.setCseq(client.getCseq() + 1);
                HttpRequest req = request.call();
                ChannelFuture future = ctx.writeAndFlush(req);
            }
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    private class PushRtpTask implements Runnable {
        private final ChannelHandlerContext ctx;

        public PushRtpTask(final ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            ctx.writeAndFlush(H264Sample.getSample());
        }
    }

}
