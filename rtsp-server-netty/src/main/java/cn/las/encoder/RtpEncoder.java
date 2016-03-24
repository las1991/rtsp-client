package cn.las.encoder;

import cn.las.mp4parser.H264Sample;
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
public class RtpEncoder extends MessageToByteEncoder<Sample> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Sample sample, ByteBuf byteBuf) throws Exception {
        ByteBuffer bb = sample.asByteBuffer();
        while (bb.remaining() > 0) {
            int length = (int) IsoTypeReaderVariable.read(bb, H264Sample.lengthSize);
            byteBuf.writeBytes((ByteBuffer) H264Sample.SEPARATOR.rewind());
            byteBuf.writeBytes((ByteBuffer) bb.slice().limit(length));
            bb.position(bb.position() + length);
        }
    }
}
