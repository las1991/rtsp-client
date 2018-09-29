package cn.las.client.handler;

import cn.las.client.AbstractClient;
import cn.las.client.ClientManager;
import cn.las.message.RtpPackage;
import cn.las.mp4parser.H264Sample;
import cn.las.rtp.RtpPacketizer;
import cn.las.rtsp.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xpertss.sdp.MediaDescription;
import xpertss.sdp.SessionDescription;
import xpertss.sdp.SessionParser;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/22
 */
public class RtspClientHandler extends ChannelInboundHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleEvt = (IdleStateEvent) evt;
            switch (idleEvt.state()) {
                case READER_IDLE:
                    logger.warn("read idle");
                    break;
                case ALL_IDLE:
                    logger.warn("all idle");
                    break;
                case WRITER_IDLE:
                    if (idleEvt.isFirst()) {
                        logger.warn("write idle");
                    } else {
                        logger.trace("write idle");
                    }
                    break;
                default:
                    logger.warn("write timeout");
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        AbstractClient.ClientSession client = ClientManager.get(ctx.channel().id().asLongText());
        logger.error("{} inactive", ctx.channel().localAddress());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof FullHttpResponse) {
                FullHttpResponse rep = (FullHttpResponse) msg;
                if (rep.status().equals(HttpResponseStatus.OK)) {
                    final AbstractClient.ClientSession client = ClientManager
                            .get(ctx.channel().id().asLongText());
                    Callable<HttpRequest> request = null;
                    if (client.getStatus() == null) {
                        request = new OptionsRequest(client);
                        client.setStatus(RtspMethods.OPTIONS);

                    } else if (client.getStatus().equals(RtspMethods.OPTIONS)) {
                        switch (client.getType()) {
                            case Push:
                                request = new AnnounceRequest(client);
                                client.setStatus(RtspMethods.ANNOUNCE);
                                break;
                            case Pull:
                                request = new DescribeRequest(client);
                                client.setStatus(RtspMethods.DESCRIBE);
                                break;
                        }

                    } else if (client.getStatus().equals(RtspMethods.DESCRIBE)
                            || client.getStatus().equals(RtspMethods.ANNOUNCE)) {
//                        client.setSession(rep.headers().get(RtspHeaderNames.SESSION));
                        if (client.getSdp() == null) {
                            ByteBuf buf = rep.content().retain();
                            try {
                                byte[] tmp = new byte[buf.readableBytes()];
                                buf.readBytes(tmp);
                                String sdp = new String(tmp);
                                SessionParser parser = new SessionParser();
                                client.setSdp(parser.parse(sdp));
                                logger.info("{}", client.getSdp());
                            } finally {
                                buf.release();
                            }
                        }
                        SessionDescription sdp = client.getSdp();
                        for (int i = 0; i < sdp.getMediaDescriptions().length; i++) {
                            if (i == client.getStreams()) {
                                MediaDescription md = sdp.getMediaDescriptions()[i];
                                Matcher matcher = Pattern.compile("^(.*)/([^/]+)$")
                                        .matcher(md.getAttribute("control").getValue());
                                if (matcher.matches()) {
                                    String name = matcher.group(2);
                                    request = new SetUpRequest(client, name);
                                }
                                client.setStreams(client.getStreams() + 1);
                                break;
                            }
                        }
                        if (client.getStreams() >= sdp.getMediaDescriptions().length) {
                            client.setStatus(RtspMethods.SETUP);
                        }
                    } else if (client.getStatus().equals(RtspMethods.SETUP)) {
                        request = new PlayRequest(client);
                        client.setStatus(RtspMethods.PLAY);
                    } else if (client.getStatus().equals(RtspMethods.PLAY)) {
                        long period = 1000 / H264Sample.videoStream.getFrame_rate();
                        ctx.executor().scheduleAtFixedRate(new RtpTask(ctx, client), 0, 20, TimeUnit.MILLISECONDS);
                        ctx.pipeline().addFirst("idle", new IdleStateHandler(0, 50, 0, TimeUnit.MILLISECONDS));
                        if (logger.isDebugEnabled()) {
                            logger.debug("{}", msg);
                        }
                    } else if (client.getStatus().equals(RtspMethods.PAUSE)) {
                    } else if (client.getStatus().equals(RtspMethods.TEARDOWN)) {
                    }
                    if (request != null) {
                        client.setCseq(client.getCseq() + 1);
                        HttpRequest req = request.call();
                        logger.debug("request {}", req);
                        ChannelFuture future = ctx.writeAndFlush(req);
                    }
                } else {
                    logger.error("response {}", rep);
                }
            } else {
                logger.error("error {}", msg);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("{}", cause.getMessage(), cause);
    }

    class RtpTask implements Runnable {

        private ChannelHandlerContext ctx;
        private AbstractClient.ClientSession clientSession;

        RtpTask(ChannelHandlerContext ctx, AbstractClient.ClientSession clientSession) {
            this.ctx = ctx;
            this.clientSession = clientSession;
        }

        @Override
        public void run() {
            Channel channel = ctx.channel();
            if (channel.isWritable()) {
                try {
                    if (clientSession.getStatus().equals(RtspMethods.PLAY)) {
                        List<RtpPackage> packages = RtpPacketizer.getVideoRtpPackages(clientSession);
                        for (final RtpPackage rtpPackage : packages) {
                            ctx.writeAndFlush(rtpPackage);
                        }
                    }
                } finally {

                }
            } else {
//                logger.error("{} not writeable", channel.localAddress());
            }
        }
    }

}
