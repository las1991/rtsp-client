package com.las.message;

/**
 * Created by las on 2016/3/24.
 */
public class RtpPackage {
    private RtpHeader header;
    private RtpBody body;
    private int channel=2;

    public RtpPackage(RtpHeader header, RtpBody body) {
        this.header = header;
        this.body = body;
    }

    public RtpHeader getHeader() {
        return header;
    }

    public void setHeader(RtpHeader header) {
        this.header = header;
    }

    public RtpBody getBody() {
        return body;
    }

    public void setBody(RtpBody body) {
        this.body = body;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }
}
