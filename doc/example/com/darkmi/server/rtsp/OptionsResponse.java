package com.darkmi.server.rtsp;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspResponseStatuses;
import io.netty.handler.codec.rtsp.RtspVersions;

import java.util.concurrent.Callable;

public class OptionsResponse implements Callable<HttpResponse> {
    private HttpRequest request = null;

    public OptionsResponse(HttpRequest request) {
        this.request = request;
    }

    @Override
    public HttpResponse call() throws Exception {
        HttpResponse response = null;
        response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.OK);
        response.headers().add(RtspHeaderNames.SERVER, "RtspServer");
        response.headers().add(RtspHeaderNames.CSEQ, this.request.headers().get(RtspHeaderNames.CSEQ));
        response.headers().add(RtspHeaderNames.PUBLIC, "SETUP,PLAY,PAUSE,TEARDOWN,GET_PARAMETER,OPTION");
        return response;
    }
}
