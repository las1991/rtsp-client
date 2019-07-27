package com.las.rtp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;

/**
 * @author cn.las
 * @date 18-9-27
 */
public class FramingRtpPacket extends DefaultByteBufHolder implements RtpChannel {

    private final int channel;
    private final int length;

    public FramingRtpPacket(int channel, int length, ByteBuf data) {
        super(data);
        this.channel = channel;
        this.length = length;
    }

    private FramingRtpPacket(ByteBuf data, FramingRtpPacket that) {
        super(data);
        this.channel = that.channel;
        this.length = that.length;
    }

    public int getChannel() {
        return channel;
    }

    public int getLength() {
        return length;
    }

    @Override
    public FramingRtpPacket duplicate() {
        return new FramingRtpPacket(content().duplicate(), this);
    }

    @Override
    public FramingRtpPacket retain() {
        return new FramingRtpPacket(content().retain(), this);
    }

    @Override
    public String toString() {
        return "FramingRtpPacket{" +
                "channel=" + channel +
                ", length=" + length +
                '}';
    }
}
