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
    private AbstractClient client;
    private Integer trackID;

    public SetUpRequest(AbstractClient client, Integer trackID) {
        this.client = client;
        this.trackID = trackID;
    }

    @Override
    public HttpRequest call() throws Exception {
        StringBuffer sb=new StringBuffer(client.getUrl());
        sb.append("/trackID=");
        sb.append(trackID);
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.SETUP, sb.toString());
        request.headers().add(RtspHeaderNames.CSEQ, client.getCseq().toString());
        if(StringUtils.isNotEmpty(client.getSession())){
            request.headers().add(RtspHeaderNames.SESSION,client.getSession());
        }
        request.headers().add(RtspHeaderNames.USER_AGENT,client.getUserAgent());
        if(client instanceof ClientPush){
            if(trackID==0){
                request.headers().add(RtspHeaderNames.TRANSPORT, "RTP/AVP/TCP;unicast;mode=receive;interleaved=0-1");
            }else {
                request.headers().add(RtspHeaderNames.TRANSPORT, "RTP/AVP/TCP;unicast;mode=receive;interleaved=2-3");
            }
        }else {
            request.headers().add(RtspHeaderNames.TRANSPORT, "RTP/AVP/TCP;unicast;");//tcp
            //udp
            //request.headers().add(RtspHeaderNames.TRANSPORT, "RTP/AVP;unicast;client_port="+client.getChannel().localAddress().toString().split(":")[1]);
        }
        return request;
    }
}
