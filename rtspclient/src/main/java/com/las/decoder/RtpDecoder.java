package com.las.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/22
 */
public class RtpDecoder extends ByteToMessageDecoder {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static byte DOLLA = 0x24;

    private int last = 0;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {

        if (in.readableBytes() > 0 && in.readByte() == DOLLA) {
            if (in.readableBytes() > 3) {
                int channel = in.readByte();
                int size = in.readUnsignedShort();
                if (in.readableBytes() >= size) {
                    ByteBuf rtp = in.readBytes(size);
                    if (channel == 0) {
                        int nalType = rtp.getByte(12) & 0x1f;
                        int nalu = 0;
                        if (nalType == 28) {
                            nalu = rtp.getByte(13) & 0x1f;
                        }
//            logger.debug("timestamp: " + rtp.getUnsignedInt(4) + " sq : " + rtp.getUnsignedShort(2)
//                + " size :" + rtp.readableBytes() + " nal type :" + nalType + (nalType == 28 ? "--"
//                + nalu : ""));
                        int sq = rtp.getUnsignedShort(2);
                        if (sq - last > 1) {
                            logger.debug("last :" + last + " new :" + sq);
                        }
                        last = sq;
                    } else {

                    }
                    rtp.release();
                } else {
                    in.readerIndex(in.readerIndex() - 4);
                }
            } else {
                in.readerIndex(in.readerIndex() - 1);
            }
        } else if (in.readableBytes() > 0) {
            in.readerIndex(in.readerIndex() - 1);
            in.retain();
//            logger.debug(ByteBufUtil.prettyHexDump(in));
            ctx.fireChannelRead(in);
        }
    }
}
