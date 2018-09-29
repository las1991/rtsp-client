package cn.las.client;

import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xpertss.sdp.SessionDescription;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author las
 * @date 18-9-29
 */
public class RtspSession {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String host;
    protected int port;
    protected String url;
    protected HttpMethod status;
    protected String sessionId;
    protected int cseq;
    protected int seq;
    protected String userAgent = "Las rtsp end system";
    protected SessionDescription sdp;
    private int streams = 0;

    public RtspSession(String url) {
        this.url = url;
        try {
            URI uri = new URI(this.url);
            this.host = uri.getHost();
            this.port = uri.getPort();
        } catch (URISyntaxException e) {
        }
    }

}
