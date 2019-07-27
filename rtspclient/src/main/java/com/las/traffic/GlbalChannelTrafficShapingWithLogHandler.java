package com.las.traffic;

import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author cn.las
 * @date 18-9-30
 */
public class GlbalChannelTrafficShapingWithLogHandler extends GlobalChannelTrafficShapingHandler {

    Logger logger = LoggerFactory.getLogger(getClass());

    public GlbalChannelTrafficShapingWithLogHandler(ScheduledExecutorService executor, long checkInterval) {
        super(executor, checkInterval);
    }

    private final List<Long> cumulativeWrittenBytes = new LinkedList<Long>();
    private final List<Long> cumulativeReadBytes = new LinkedList<Long>();
    private final List<Long> throughputWrittenBytes = new LinkedList<Long>();
    private final List<Long> throughputReadBytes = new LinkedList<Long>();

    /**
     * Override to compute average of bandwidth between all channels
     */
    @Override
    protected void doAccounting(TrafficCounter counter) {
        long maxWrittenNonZero = this.maximumCumulativeWrittenBytes();
        if (maxWrittenNonZero == 0) {
            maxWrittenNonZero = 1;
        }
        long maxReadNonZero = this.maximumCumulativeReadBytes();
        if (maxReadNonZero == 0) {
            maxReadNonZero = 1;
        }
        for (TrafficCounter tc : this.channelTrafficCounters()) {
            long cumulativeWritten = tc.cumulativeWrittenBytes();
            if (cumulativeWritten > maxWrittenNonZero) {
                cumulativeWritten = maxWrittenNonZero;
            }
            cumulativeWrittenBytes.add((maxWrittenNonZero - cumulativeWritten) * 100 / maxWrittenNonZero);
            throughputWrittenBytes.add(tc.getRealWriteThroughput() >> 10);
            long cumulativeRead = tc.cumulativeReadBytes();
            if (cumulativeRead > maxReadNonZero) {
                cumulativeRead = maxReadNonZero;
            }
            cumulativeReadBytes.add((maxReadNonZero - cumulativeRead) * 100 / maxReadNonZero);
            throughputReadBytes.add(tc.lastReadThroughput() >> 10);
        }
        logger.info(new StringBuilder().append(this.toString()).append(" QueuesSize: ").append(queuesSize())
                .append("\nWrittenBytesPercentage: ").append(cumulativeWrittenBytes)
                .append("\nWrittenThroughputBytes: ").append(throughputWrittenBytes)
                .append("\nReadBytesPercentage: ").append(cumulativeReadBytes)
                .append("\nReadThroughputBytes: ").append(throughputReadBytes).toString());
        cumulativeWrittenBytes.clear();
        cumulativeReadBytes.clear();
        throughputWrittenBytes.clear();
        throughputReadBytes.clear();
        super.doAccounting(counter);
    }
}
