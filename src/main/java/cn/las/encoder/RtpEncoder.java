package cn.las.encoder;

import cn.las.message.RtpPackage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/24
 */
public class RtpEncoder extends MessageToByteEncoder<RtpPackage> {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static byte DOLLA = 0x24;

    @Override
    protected void encode(ChannelHandlerContext ctx, RtpPackage rtpPackage, ByteBuf out) throws Exception {
        Integer length = rtpPackage.getHeader().getRtpHeader().length;
        if (rtpPackage.getBody().getFuIndicator() != null
                && rtpPackage.getBody().getFuHeader() != null) {
            length += 1 + 1 + rtpPackage.getBody().getData().length;
        } else {
            length += 1 + rtpPackage.getBody().getData().length;
        }
//        logger.debug("encode length:" + length);
        ByteBuf byteBuf = null;
        try {
            byteBuf = out.alloc().directBuffer(length);
            byteBuf.writeByte(DOLLA);
            byteBuf.writeByte(rtpPackage.getChannel());
            byteBuf.writeShort(length);
            byteBuf.writeBytes(rtpPackage.getHeader().getRtpHeader());
            if (rtpPackage.getBody().getFuIndicator() != null
                    && rtpPackage.getBody().getFuHeader() != null) {
                byteBuf.writeByte(rtpPackage.getBody().getFuIndicator().getFuIndicator());
                byteBuf.writeByte(rtpPackage.getBody().getFuHeader().getFuHeader());
            } else {
                byteBuf.writeByte(rtpPackage.getBody().getNaluHeader().getNaluHeader());
            }
            byteBuf.writeBytes(rtpPackage.getBody().getData());
            out.writeBytes(byteBuf);
        } finally {
            byteBuf.release();
        }
    }
}
