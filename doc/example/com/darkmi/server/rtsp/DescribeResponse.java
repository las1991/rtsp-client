package com.darkmi.server.rtsp;

import com.darkmi.server.core.RtspController;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspResponseStatuses;
import io.netty.handler.codec.rtsp.RtspVersions;

import java.util.concurrent.Callable;

public class DescribeResponse implements Callable<HttpResponse> {
    private HttpRequest request = null;

    public DescribeResponse(HttpRequest request) {
        this.request = request;
    }

    public HttpResponse call() throws Exception {
        HttpResponse response = null;
        response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.OK);
        response.headers().set(RtspHeaderNames.SERVER, RtspController.SERVER);
        response.headers().set(RtspHeaderNames.CSEQ,
                this.request.headers().get(RtspHeaderNames.CSEQ));
        response.headers().set(RtspHeaderNames.CONTENT_LENGTH, "0");
        return response;
    }
}
