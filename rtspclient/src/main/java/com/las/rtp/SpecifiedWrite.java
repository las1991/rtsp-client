package cn.las.rtp;


import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

/**
 * @author las
 */
public interface SpecifiedWrite {

    SpecifiedWrite writeByte(int value);

    SpecifiedWrite writeShort(int value);

    SpecifiedWrite writeBytes(ByteBuf buf);

    SpecifiedWrite writeBytes(byte[] buf);

    SpecifiedWrite writeBytes(ByteBuffer buf);

}
