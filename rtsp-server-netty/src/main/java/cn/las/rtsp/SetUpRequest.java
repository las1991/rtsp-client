package cn.las.rtsp;

import cn.las.client.Client;
import io.netty.handler.codec.http.DefaultHttpRequest;
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
    private Client client;

    public SetUpRequest(Client client) {
        this.client = client;
    }

    @Override
    public HttpRequest call() throws Exception {
        DefaultHttpRequest request = new DefaultHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.SETUP, client.getUrl()+"/trackID=1");
        request.headers().add(RtspHeaderNames.CSEQ, client.getCseq().toString());
        request.headers().add(RtspHeaderNames.USER_AGENT,"LibVLC/2.2.1 (LIVE555 Streaming Media v2014.07.25)");
        request.headers().add(RtspHeaderNames.TRANSPORT, "RTP/AVP/TCP;unicast;interleaved=0-1");
        return request;
    }
}
