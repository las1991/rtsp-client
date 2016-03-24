package cn.las.message;

import java.nio.ByteBuffer;

/**
 * Created by las on 2016/3/24.
 */
public class RtpMessage {
    private RtpHeader header;
    private ByteBuffer body;

    public RtpMessage(RtpHeader header, ByteBuffer body) {
        this.header = header;
        this.body = body;
    }

    public RtpHeader getHeader() {
        return header;
    }

    public void setHeader(RtpHeader header) {
        this.header = header;
    }

    public ByteBuffer getBody() {
        return body;
    }

    public void setBody(ByteBuffer body) {
        this.body = body;
    }
}
