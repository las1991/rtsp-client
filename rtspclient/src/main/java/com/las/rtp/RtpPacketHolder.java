package com.las.rtp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

import java.nio.ByteBuffer;

/**
 * @author las
 * @date 18-9-20
 */
public class RtpPacketHolder implements RtpPacket {

    private final RtpHeader header;
    private ByteBuf payload;

    public RtpPacketHolder(RtpHeader header, ByteBuf content) {
        this.header = header;
        this.payload = content;
    }

    @Override
    public RtpHeader header() {
        return header;
    }

    @Override
    public ByteBuf content() {
        return payload;
    }

    @Override
    public RtpPacketHolder copy() {
        return new RtpPacketHolder(header.copy(), payload.copy());
    }

    @Override
    public RtpPacketHolder duplicate() {
        return new RtpPacketHolder(header.copy(), payload.duplicate());
    }

    @Override
    public ByteBufHolder retainedDuplicate() {
        return duplicate().retain();
    }

    @Override
    public ByteBufHolder replace(ByteBuf content) {
        return new RtpPacketHolder(header.copy(), content);
    }

    @Override
    public int refCnt() {
        return payload.refCnt();
    }

    @Override
    public RtpPacketHolder retain() {
        payload.retain();
        return this;
    }

    @Override
    public RtpPacketHolder retain(int increment) {
        payload.retain(increment);
        return this;
    }

    @Override
    public ByteBufHolder touch() {
        payload.touch();
        return this;
    }

    @Override
    public ByteBufHolder touch(Object hint) {
        payload.touch(hint);
        return this;
    }

    @Override
    public boolean release() {
        return payload.release();
    }

    @Override
    public boolean release(int decrement) {
        return payload.release(decrement);
    }

    @Override
    public RtpPacketHolder writeByte(int value) {
        payload.writeByte(value);
        return this;
    }

    @Override
    public RtpPacketHolder writeShort(int value) {
        payload.writeShort(value);
        return this;
    }

    @Override
    public RtpPacketHolder writeBytes(ByteBuf buf) {
        payload.writeBytes(buf);
        return this;
    }

    @Override
    public RtpPacketHolder writeBytes(byte[] buf) {
        payload.writeBytes(buf);
        return this;
    }

    @Override
    public RtpPacketHolder writeBytes(ByteBuffer buf) {
        payload.writeBytes(buf);
        return this;
    }

    @Override
    public String toString() {
        return "RtpPacketHolder{" +
                "header=" + header +
                ", payload=" + payload +
                '}';
    }
}
