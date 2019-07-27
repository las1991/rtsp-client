package com.las.rtp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

import java.nio.ByteBuffer;

/**
 * @author las
 * @date 18-10-10
 */
public interface RtpPacket extends ByteBufHolder, SpecifiedWrite {

    RtpHeader header();

    @Override
    RtpPacket copy();

    @Override
    RtpPacket duplicate();

    @Override
    RtpPacket retain();

    @Override
    RtpPacket retain(int increment);

    @Override
    RtpPacket writeByte(int value);

    @Override
    RtpPacket writeShort(int value);

    @Override
    RtpPacket writeBytes(ByteBuf buf);

    @Override
    RtpPacket writeBytes(byte[] buf);

    @Override
    RtpPacket writeBytes(ByteBuffer buf);
}
