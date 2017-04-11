package cn.las.stream;

import java.nio.ByteBuffer;

/**
 * Created by las on 2017/4/10.
 */
public class VideoStream extends MediaStream {
    long duration;

    long timescale;

    long frame_rate;

    ByteBuffer sps;

    ByteBuffer pps;

    int SampleLengthSize;

    
    @Override
    public int getSSRC() {
        return 0;
    }

    public long getFrame_rate() {
        return frame_rate;
    }

    public void setFrame_rate(long frame_rate) {
        this.frame_rate = frame_rate;
    }

    public long getTimescale() {
        return timescale;
    }

    public void setTimescale(long timescale) {
        this.timescale = timescale;
    }

    public ByteBuffer getSps() {
        return sps;
    }

    public void setSps(ByteBuffer sps) {
        this.sps = sps;
    }

    public ByteBuffer getPps() {
        return pps;
    }

    public void setPps(ByteBuffer pps) {
        this.pps = pps;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getSampleLengthSize() {
        return SampleLengthSize;
    }

    public void setSampleLengthSize(int sampleLengthSize) {
        SampleLengthSize = sampleLengthSize;
    }
}
