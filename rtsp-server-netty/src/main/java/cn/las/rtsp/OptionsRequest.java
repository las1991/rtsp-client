package cn.las.rtsp;

import cn.las.client.AbstractClient;
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

    private AbstractClient.ClientSession client;

    public OptionsRequest(AbstractClient.ClientSession client) {
        this.client = client;
    }

    @Override
    public HttpRequest call() throws Exception {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.OPTIONS, client.getUrl());
        request.headers().add(RtspHeaderNames.CSEQ, client.getCseq().toString());
        return request;
    }
}
