package cn.las.rtsp;

import cn.las.client.AbstractClient;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.handler.codec.rtsp.RtspVersions;

import java.util.concurrent.Callable;

/**
 * Created by las on 2016/3/26.
 */
public class AnnounceRequest implements Callable<HttpRequest> {

    private final static String sdp="v=0\n" +
            "o=- 956018329 956018329 IN IP4 127.0.0.1\n" +
            "s=liveSengledIPC\n" +
            "i=LIVE555 Streaming M\n" +
            "c=IN IP4 127.0.0.1\n" +
            "t=0 0\n" +
            "a=x-qt-text-nam:54.223.242.201\n" +
            "a=x-qt-text-inf:liveSengledIPC\n" +
            "a=x-qt-text-cmt:source application:LIVE555 Streaming Media\n" +
            "a=x-qt-text-aut:DarwinInjector\n" +
            "a=x-qt-text-cpy:\n" +
            "m=video 0 RTP/AVP 97\n" +
            "a=rtpmap:97 H264/90000\n" +
            "a=control:trackID=1\n"+
            "m=audio 0 RTP/AVP 8\n"+
            "a=3GPP-Adaptation-Support:1\n" +
            "a=ptime:20\n" +
            "a=control:trackID=2\n"  ;
    /**
     *
     */

    private AbstractClient client;

    public AnnounceRequest(AbstractClient client) {
        this.client = client;
    }

    @Override
    public HttpRequest call() throws Exception {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.ANNOUNCE, client.getUrl());
        request.headers().add(RtspHeaderNames.CSEQ, client.getCseq().toString());
        request.headers().add(RtspHeaderNames.USER_AGENT, client.getUserAgent());
        request.headers().add(RtspHeaderNames.ACCEPT, "application/sdp");
        request.content().writeBytes(sdp.getBytes());
        request.headers().add(RtspHeaderNames.CONTENT_LENGTH,request.content().readableBytes()+"");
        return request;
    }

}
