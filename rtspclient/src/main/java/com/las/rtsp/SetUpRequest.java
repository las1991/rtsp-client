package com.las.rtsp;

import com.las.client.RtspSession;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.handler.codec.rtsp.RtspVersions;

import java.util.concurrent.Callable;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/22
 */
public class SetUpRequest implements Callable<HttpRequest> {
    private RtspSession session;
    private String track;

    public SetUpRequest(RtspSession session, String track) {
        this.session = session;
        this.track = track;
    }

    @Override
    public HttpRequest call() {
        StringBuffer sb = new StringBuffer(session.getUrl());
        sb.append("/");
        sb.append(track);
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.SETUP, sb.toString());
        request.headers().add(RtspHeaderNames.CSEQ, session.getCseq());

        request.headers().add(RtspHeaderNames.USER_AGENT, session.getUserAgent());
        int trackID = 0;
        try {
            trackID = Integer.parseInt(track.split("=")[1]);
        } catch (Exception e) {
        }
        request.headers().add(RtspHeaderNames.TRANSPORT, "RTP/AVP/TCP;unicast;interleaved=" + trackID * 2 + "-" + (trackID * 2 + 1));
        return request;
    }
}
