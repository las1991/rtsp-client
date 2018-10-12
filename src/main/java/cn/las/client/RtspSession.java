package cn.las.client;

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
    protected String token;
    protected String url;
    private RtspState state = RtspState.OPTIONS;
    protected String sessionId;
    protected int cseq;
    protected int seq;
    protected String userAgent = "Las rtsp end system";
    protected SessionDescription sdp;

    private int setupStreams;

    public RtspSession(String url) {
        this.url = url;
        try {
            URI uri = new URI(this.url);
            this.host = uri.getHost();
            this.port = uri.getPort();
            this.token = uri.getPath();
        } catch (URISyntaxException e) {
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getToken() {
        return token;
    }

    public String getUrl() {
        return url;
    }

    public RtspState getState() {
        return state;
    }

    public void setState(RtspState state) {
        this.state = state;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getCseq() {
        return cseq;
    }

    public void setCseq(int cseq) {
        this.cseq = cseq;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public SessionDescription getSdp() {
        return sdp;
    }

    public void setSdp(SessionDescription sdp) {
        this.sdp = sdp;
    }

    public int getSetupStreams() {
        return setupStreams;
    }

    public void setSetupStreams(int setupStreams) {
        this.setupStreams = setupStreams;
    }
}
