package cn.las.rtsp;

import cn.las.client.AbstractClient;
import cn.las.client.ClientPush;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
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
public class SetUpRequest implements Callable<HttpRequest> {
    private AbstractClient.ClientSession client;
    private String track;

    public SetUpRequest(AbstractClient.ClientSession client, String track) {
        this.client = client;
        this.track = track;
    }

    @Override
    public HttpRequest call() throws Exception {
        StringBuffer sb = new StringBuffer(client.getUrl());
        sb.append("/");
        sb.append(track);
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.SETUP, sb.toString());
        request.headers().add(RtspHeaderNames.CSEQ, client.getCseq().toString());
        if (StringUtils.isNotEmpty(client.getSession())) {
            request.headers().add(RtspHeaderNames.SESSION, client.getSession());
        }
        request.headers().add(RtspHeaderNames.USER_AGENT, client.getUserAgent());
        int trackID = 0;
        try {
            trackID = Integer.parseInt(track.split("=")[1]);
        } catch (Exception e) {
        }
        request.headers().add(RtspHeaderNames.TRANSPORT, "RTP/AVP/TCP;unicast;interleaved=" + trackID * 2 + "-" + (trackID * 2 + 1));
        return request;
    }
}
