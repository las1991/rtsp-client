package cn.las.client.handler;

import cn.las.client.Player;
import cn.las.rtp.FramingRtpPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class FramingRtpPacketHandler extends SimpleChannelInboundHandler<FramingRtpPacket> {
    private final Player player;

    public FramingRtpPacketHandler(Player player) {
        this.player = player;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FramingRtpPacket msg) throws Exception {
        player.onFramingRtpPacket(msg);
    }
}
