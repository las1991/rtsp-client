package cn.las.client.handler;

import cn.las.client.Player;
import cn.las.rtp.FramingRtpPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RtpHandler extends SimpleChannelInboundHandler<FramingRtpPacket> {
    private final Player player;

    public RtpHandler(Player player) {
        this.player = player;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FramingRtpPacket msg) throws Exception {
        player.hasChanged();
        player.notifyObservers(msg.retain());
    }
}