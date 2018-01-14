package cn.las.rtsp;

import cn.las.client.AbstractClient;
import cn.las.client.ClientPush;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
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
public class PlayRequest implements Callable<HttpRequest> {
    private AbstractClient.ClientSession client;

    public PlayRequest(AbstractClient.ClientSession client) {
        this.client = client;
    }

    @Override
    public HttpRequest call() throws Exception {
        HttpMethod method = client.getType().equals(AbstractClient.ClientSession.Type.Push) ? RtspMethods.RECORD : RtspMethods.PLAY;
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, method, client.getUrl());
        request.headers().add(RtspHeaderNames.CSEQ, client.getCseq().toString());
        if (null != client.getSession()) {
            request.headers().add(RtspHeaderNames.SESSION, client.getSession());
        }
        request.headers().add(RtspHeaderNames.USER_AGENT, client.getUserAgent());
        request.headers().add(RtspHeaderNames.RANGE, "npt=0.000-");
        return request;
    }
}
