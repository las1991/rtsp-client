package cn.las.rtsp;

import cn.las.client.RtspSession;
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
public class OptionsRequest implements Callable<HttpRequest> {

    private RtspSession session;

    public OptionsRequest(RtspSession session) {
        this.session = session;
    }

    @Override
    public HttpRequest call() {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.OPTIONS, session.getUrl());
        request.headers().add(RtspHeaderNames.CSEQ, session.getCseq());
        return request;
    }
}
