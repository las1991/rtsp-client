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
public class DescribeRequest implements Callable<HttpRequest> {
    private RtspSession session;

    public DescribeRequest(RtspSession session) {
        this.session = session;
    }

    @Override
    public HttpRequest call() throws Exception {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.DESCRIBE, session.getUrl());
        request.headers().add(RtspHeaderNames.CSEQ, session.getCseq());
        request.headers().add(RtspHeaderNames.USER_AGENT, session.getUserAgent());
        request.headers().add(RtspHeaderNames.ACCEPT, "application/sdp");
        return request;
    }

    /**
     *
     o=- 71453575 71453575 IN IP4 219.232.105.96
     s=liveSengledIPC
     i=LIVE555 Streaming Media
     c=IN IP4 219.232.105.96
     t=0 0
     a=x-qt-text-nam:101.68.222.220
     a=x-qt-text-inf:liveSengledIPC
     a=x-qt-text-cmt:source application:LIVE555 Streaming Media
     a=x-qt-text-aut:DarwinInjector
     a=x-qt-text-cpy
     m=video 0 RTP/AVP 97
     i=
     a=rtpmap:97 H264/90000
     a=control:rtsp://101.68.222.220:554/2C34AD35906AAE6360B52BCB6B94F674/stream=0
     a=codecpts:1
     a=fw_version:2.2.118
     m=audio 0 RTP/AVP 8
     i=
     a=ptime:20
     a=control:rtsp://101.68.222.220:554/2C34AD35906AAE6360B52BCB6B94F674/stream=1
     */

}
