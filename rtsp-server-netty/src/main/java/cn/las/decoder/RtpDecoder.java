package cn.las.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/22
 */
public class RtpDecoder extends ByteToMessageDecoder {

    private final static byte DOLLA = 0x24;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        ByteBuf byteBuf = in.copy();
        byte[] req = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(req);
        if (req.length>0&&req[0] == DOLLA) {
            Integer channel = new Byte(req[1]).intValue();
            Integer size = new Integer(((req[2] & 0xFF) << 8) | ((req[3] & 0xFF))) + 4;
            Integer sequence = new Integer(((req[6] & 0xFF) << 8) | ((req[7] & 0xFF)));
            System.out.println(sequence + ":" + size);

            if (in.readableBytes() >= size) {
                byte[] cache = new byte[size];
                in.readBytes(cache);
            }
        } else {
            System.out.println(new String(req, "UTF-8"));
            in.retain();
            ctx.fireChannelRead(in);
        }
    }
}
