package cn.las.rtsp;

import cn.las.client.Client;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspHeaderValues;
import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.handler.codec.rtsp.RtspVersions;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.Callable;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/22
 */
public class PlayRequest implements Callable<HttpRequest>{
    private Client client;

    public PlayRequest(Client client) {
        this.client = client;
    }

    @Override
    public HttpRequest call() throws Exception {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.PLAY, client.getUrl());
        request.headers().add(RtspHeaderNames.CSEQ, client.getCseq().toString());
        request.headers().add(RtspHeaderNames.SESSION,client.getSession());
        request.headers().add(RtspHeaderNames.USER_AGENT,"LibVLC/2.2.2 (LIVE555 Streaming Media v2016.01.12)");
        request.headers().add(RtspHeaderNames.RANGE, "npt=0.000-");
        return request;
    }
}