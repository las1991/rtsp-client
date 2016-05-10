package com.darkmi.server.rtsp;

import com.darkmi.server.util.DateUtil;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.handler.codec.rtsp.RtspVersions;

import java.util.concurrent.Callable;

public class AnnounceAction implements Callable<HttpRequest> {
    private HttpRequest originRequest = null;

    public AnnounceAction(HttpRequest request) {
        originRequest = request;
    }

    @Override
    public HttpRequest call() throws Exception {
        HttpRequest request = null;
        request =
                new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.ANNOUNCE, originRequest.uri());
        request.headers().set(RtspHeaderNames.CSEQ, request.headers().get(RtspHeaderNames.CSEQ));
        request.headers().set(RtspHeaderNames.DATE, DateUtil.getGmtDate());
        request.headers().set(RtspHeaderNames.SESSION, request.headers().get(RtspHeaderNames.SESSION));
        request.headers().set("Notice", "1103 \"Stream Stalled\" event-date=20000406T091645Z");
        return request;
    }
}
