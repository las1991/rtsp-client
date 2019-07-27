package com.las.traffic;

import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @author las
 * @date 18-9-30
 */
public class Traffic {

    private static GlobalTrafficShapingHandler globalTrafficShapingHandler;
    private static GlobalChannelTrafficShapingHandler globalChannelTrafficShapingHandler;

    public static GlobalTrafficShapingHandler globalTrafficShapingHandler(ScheduledExecutorService executor) {
        if (globalTrafficShapingHandler == null) {
            globalTrafficShapingHandler = new GlbalTrafficShapingWithLogHandler(executor, 5 * 1000);
        }

        return globalTrafficShapingHandler;
    }

    public static GlobalChannelTrafficShapingHandler globalChannelTrafficShapingHandler(ScheduledExecutorService executor) {
        if (globalChannelTrafficShapingHandler == null) {
            globalChannelTrafficShapingHandler = new GlbalChannelTrafficShapingWithLogHandler(executor, 1000);
        }

        return globalChannelTrafficShapingHandler;
    }
}
