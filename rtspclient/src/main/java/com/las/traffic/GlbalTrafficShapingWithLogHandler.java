package com.las.traffic;

import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @author las
 * @date 18-9-30
 */
public class GlbalTrafficShapingWithLogHandler extends GlobalTrafficShapingHandler {

    Logger logger = LoggerFactory.getLogger(getClass());

    public GlbalTrafficShapingWithLogHandler(ScheduledExecutorService executor, long checkInterval) {
        super(executor, checkInterval);
    }

    /**
     * Override to compute average of bandwidth between all channels
     */
    @Override
    protected void doAccounting(TrafficCounter trafficCounter) {
        double wr = (double) trafficCounter.lastWriteThroughput() / (double) trafficCounter.lastReadThroughput();
        StringBuilder builder = new StringBuilder()
                .append("w/r : ").append(wr).append(", ")
                .append("Current Speed Read: ").append(trafficCounter.lastReadThroughput() >> 10).append(" KB/s, ")
                .append("Asked Write: ").append(trafficCounter.lastWriteThroughput() >> 10).append(" KB/s, ")
                .append("Real Write: ").append(trafficCounter.getRealWriteThroughput() >> 10).append(" KB/s, ")
                .append("Current Read: ").append(trafficCounter.currentReadBytes() >> 10).append(" KB, ")
                .append("Current asked Write: ").append(trafficCounter.currentWrittenBytes() >> 10).append(" KB, ")
                .append("Current real Write: ").append(trafficCounter.getRealWrittenBytes().get() >> 10).append(" KB");
        logger.info("{}", builder.toString());
        super.doAccounting(trafficCounter);
    }
}
