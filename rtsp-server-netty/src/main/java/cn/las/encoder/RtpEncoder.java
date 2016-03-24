package cn.las.encoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/24
 */
public class RtpEncoder extends MessageToMessageEncoder<Byte[]> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Byte msg[], List<Object> list) throws Exception {
        list.add(msg);
    }
}
