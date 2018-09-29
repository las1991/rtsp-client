package cn.las.client;

import cn.las.rtp.FramingRtpPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.Observer;

public class PlayerLogger implements Observer {

    Logger logger = LoggerFactory.getLogger(getClass());

    private final Player player;

    public PlayerLogger(Player player) {
        this.player = player;
        player.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Player && arg instanceof FramingRtpPacket) {
            FramingRtpPacket rtpPacket = (FramingRtpPacket) arg;
            logger.trace("{}", rtpPacket);
        }
    }
}
