package cn.las.client.Handler;

import cn.las.client.AbstractClient;
import cn.las.client.ClientManager;
import cn.las.client.ClientPush;
import cn.las.message.RtpPackage;
import cn.las.mp4parser.H264Sample;
import cn.las.rtp.RtpPacketizer;
import cn.las.rtsp.*;
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
import org.mp4parser.muxer.Sample;

import java.util.List;
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
        System.out.println(rep);
        if (rep.status().equals(HttpResponseStatus.OK)) {
            AbstractClient client = ClientManager.get(ctx.channel().id().asLongText());
            Callable<HttpRequest> request = null;
            if (client.getStatus() == null) {
                request = new OptionsRequest(client);
                client.setStatus(RtspMethods.OPTIONS);

            } else if (client.getStatus().equals(RtspMethods.OPTIONS)) {
                if (client instanceof ClientPush) {
                    request = new AnnounceRequest(client);
                    client.setStatus(RtspMethods.ANNOUNCE);
                } else {
                    request = new DescribeRequest(client);
                    client.setStatus(RtspMethods.DESCRIBE);
                }
            } else if (client.getStatus().equals(RtspMethods.DESCRIBE)
                    || client.getStatus().equals(RtspMethods.ANNOUNCE)) {
                ByteBuf buf = rep.content();
                client.getSdp().load(new ByteBufInputStream(buf));
                request = new SetUpRequest(client, 1);
                client.setStatus(RtspMethods.SETUP);

            } else if (client.getStatus().equals(RtspMethods.SETUP)) {
                client.setSession(rep.headers().get(RtspHeaderNames.SESSION).toString());
                System.out.println("session : " + client.getSession());
                if (StringUtils.isEmpty(client.getSession())) {
                    request = new SetUpRequest(client, 2);
                } else {
                    request = new PlayRequest(client);
                    client.setStatus(RtspMethods.PLAY);
                }
            } else if (client.getStatus().equals(RtspMethods.PLAY)) {
                if (client instanceof ClientPush) {
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
                System.out.println(req);
                ChannelFuture future = ctx.writeAndFlush(req);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    private class PushRtpTask implements Runnable {
        long timestamp = 1l;
        int seq = 1;
        private final ChannelHandlerContext ctx;


        public PushRtpTask(final ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            ClientPush clientPush = (ClientPush) ClientManager.get(ctx.channel().id().asLongText());
            Sample sample = H264Sample.getSample();
            List<RtpPackage> packages = RtpPacketizer.getRtpPackages(sample, clientPush);
            for (RtpPackage rtpPackage : packages) {
                ctx.writeAndFlush(rtpPackage);
            }
            timestamp++;
            seq++;
        }
    }

}
