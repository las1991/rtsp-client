package cn.las.client.handler;

import cn.las.client.Player;
import cn.las.rtp.FramingRtpPacket;
import cn.las.rtp.InterleavedRtpPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class InterleavedRtpPacketHandler extends SimpleChannelInboundHandler<InterleavedRtpPacket> {
    private final Player player;

    public InterleavedRtpPacketHandler(Player player) {
        this.player = player;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InterleavedRtpPacket msg) throws Exception {
        player.onInterleavedRtpPacket(msg);
    }
}
