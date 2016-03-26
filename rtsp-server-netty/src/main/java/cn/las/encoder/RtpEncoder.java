package cn.las.encoder;

import cn.las.message.RtpPackage;
import cn.las.mp4parser.H264Sample;
import cn.las.rtp.RtpPacketizer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.mp4parser.muxer.Sample;
import org.mp4parser.tools.IsoTypeReaderVariable;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/24
 */
public class RtpEncoder extends MessageToByteEncoder<RtpPackage> {

    private final static byte DOLLA = 0x24;

    @Override
    protected void encode(ChannelHandlerContext ctx, RtpPackage rtpPackage, ByteBuf byteBuf) throws Exception {
        Integer length=0;
        if(rtpPackage.getBody().getFuIndicator()!=null
                &&rtpPackage.getBody().getFuHeader()!=null){
            length=12+1+1+rtpPackage.getBody().getData().length;
        }else {
            length=12+rtpPackage.getBody().getData().length;
        }
        byteBuf.writeByte(DOLLA);
        byteBuf.writeByte((byte) 0x00);
        byteBuf.writeShort(length+4);
        if(rtpPackage.getBody().getFuIndicator()!=null
                &&rtpPackage.getBody().getFuHeader()!=null){
            byteBuf.writeByte(rtpPackage.getBody().getFuIndicator().getFuIndicator());
            byteBuf.writeByte(rtpPackage.getBody().getFuHeader().getFuHeader());
        }
        byteBuf.writeBytes(rtpPackage.getBody().getData());
    }
}
