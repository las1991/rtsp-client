package cn.las.client.Handler;

import cn.las.client.AbstractClient;
import cn.las.client.ClientManager;
import cn.las.message.RtpPackage;
import cn.las.rtp.RtpPacketizer;
import cn.las.rtsp.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import xpertss.sdp.MediaDescription;
import xpertss.sdp.SessionDescription;
import xpertss.sdp.SessionParser;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/22
 */
public class RtspClientHandler extends ChannelInboundHandlerAdapter {

    Logger logger = Logger.getLogger(this.getClass());

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleEvt = (IdleStateEvent) evt;
            switch (idleEvt.state()) {
                case READER_IDLE:
                    logger.debug("read idle");
                    break;
                case ALL_IDLE:
                    logger.debug("all idle");
                    break;
                case WRITER_IDLE:
                    Channel channel = ctx.channel();
                    final AbstractClient.ClientSession clientSession = ClientManager.get(channel.id().asLongText());
                    logger.debug(clientSession.getUrl() + "   push rtp ,channel : " + channel.hashCode() + " , active : " + channel.isActive());
                    if (channel.isActive()) {
                        List<RtpPackage> packages = RtpPacketizer.getRtpPackages(clientSession);
                        for (final RtpPackage rtpPackage : packages) {
                            channel.writeAndFlush(rtpPackage);
                        }
                    } else {
                        logger.error(clientSession.getUrl() + " channel unactive");
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
        logger.error("client end at:" + ctx.channel().localAddress() + " ,remote : " + client.getUrl());
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
                    final AbstractClient.ClientSession client = ClientManager.get(ctx.channel().id().asLongText());
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
                        client.setSession(rep.headers().get(RtspHeaderNames.SESSION).toString());
                        if (client.getSdp() == null) {
                            ByteBuf buf = rep.content().retain();
                            try {
                                byte[] tmp = new byte[buf.readableBytes()];
                                buf.readBytes(tmp);
                                String sdp = new String(tmp);
                                SessionParser parser = new SessionParser();
                                client.setSdp(parser.parse(sdp));
                                logger.info(client.getSdp());
                            } finally {
                                buf.release();
                            }
                        }
                        SessionDescription sdp = client.getSdp();
                        for (int i = 0; i < sdp.getMediaDescriptions().length; i++) {
                            if (i == client.getStreams()) {
                                MediaDescription md = sdp.getMediaDescriptions()[i];
                                Matcher matcher = Pattern.compile("^(.*)/([^/]+)$").matcher(md.getAttribute("control").getValue());
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
                        logger.debug(msg);
                    } else if (client.getStatus().equals(RtspMethods.PAUSE)) {
                    } else if (client.getStatus().equals(RtspMethods.TEARDOWN)) {
                    }
                    if (request != null) {
                        client.setCseq(client.getCseq() + 1);
                        HttpRequest req = request.call();
                        logger.debug(req);
                        ChannelFuture future = ctx.writeAndFlush(req);
                    }
                } else {
                    logger.error(rep);
                }
            } else {
                logger.error(msg);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(), cause);
    }

}
