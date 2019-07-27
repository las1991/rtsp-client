package com.las.encoder;

import com.las.rtp.FramingRtpPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author las
 * @date 18-9-26
 */
public class RtpOverTcpEncoder extends MessageToByteEncoder<FramingRtpPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, FramingRtpPacket src, ByteBuf out) {
        outputFrame(src, out);
    }

    private void outputFrame(FramingRtpPacket frame, ByteBuf out) {
        out.writeByte('$');
        out.writeByte(frame.getChannel());
        out.writeShort(frame.content().readableBytes());
        out.writeBytes(frame.content());
    }
}
