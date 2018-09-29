package cn.las.client.handler;

import cn.las.client.*;
import cn.las.rtsp.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xpertss.sdp.MediaDescription;
import xpertss.sdp.SessionDescription;
import xpertss.sdp.SessionParser;

import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/22
 */
public class RtspClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Client client;

    public RtspClientHandler(Client client) {

        this.client = client;
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
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) throws Exception {

        if (logger.isTraceEnabled()) {
            logger.trace("response {}", response);
        }
        if (!response.status().equals(HttpResponseStatus.OK)) {
            logger.error("bad response {}", response);
            ctx.close();
            return;
        }

        Callable<HttpRequest> request = null;
        RtspSession session = client.session();

        switch (session.getState()) {
            case OPTIONS:
                if (client instanceof Player) {
                    request = new DescribeRequest(session);
                    session.setState(RtspState.DESCRIBE);
                }
                if (client instanceof Recorder) {
                    request = new AnnounceRequest(session);
                    session.setState(RtspState.ANNOUNCE);
                }
                break;
            case ANNOUNCE:
            case DESCRIBE:
                if (client instanceof Player){
                    ByteBuf buf = response.content();
                    byte[] tmp = new byte[buf.readableBytes()];
                    buf.readBytes(tmp);
                    String sdpStr = new String(tmp);
                    SessionParser parser = new SessionParser();
                    session.setSdp(parser.parse(sdpStr));
                    logger.info("{}", session.getSdp());
                }

                session.setState(RtspState.SETUP);
            case SETUP:
                SessionDescription sdp = session.getSdp();
                final int setupStreams = session.getSetupStreams();
                for (int i = 0; i < sdp.getMediaDescriptions().length; i++) {
                    if (i == setupStreams) {
                        MediaDescription md = sdp.getMediaDescriptions()[i];
                        Matcher matcher = Pattern.compile("streamid=(\\d+)$")
                                .matcher(md.getAttribute("control").getValue());
                        if (matcher.matches()) {
                            String name = matcher.group(1);
                            request = new SetUpRequest(session, "streamid="+name);
                        }
                        break;
                    }
                }
                int next = setupStreams + 1;
                if (next == sdp.getMediaDescriptions().length) {
                    if (client instanceof Player) {
                        request = new PlayRequest(session);
                        session.setState(RtspState.PLAY);
                    }
                    if (client instanceof Recorder) {
                        request = new RecodeRequest(session);
                        session.setState(RtspState.RECORDE);
                    }
                }
                break;
            case PLAY:
                Player player = (Player) client;
                player.doPlay();
                break;
            case RECORDE:
                Recorder recorder = (Recorder) client;
                recorder.doRecoder();
                break;
            case PAUSE:
                break;
            case TEARDOWN:
                break;
            default:

        }

        if (request == null) {
            return;
        }

        session.setCseq(session.getCseq() + 1);
        HttpRequest req = request.call();
        if (StringUtils.isNotEmpty(session.getSessionId())) {
            req.headers().add(RtspHeaderNames.SESSION, session.getSessionId());
        }
        if (logger.isTraceEnabled()) {
            logger.trace("request {}", req);
        }
        ctx.writeAndFlush(req);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("{}", cause.getMessage(), cause);
    }

}
