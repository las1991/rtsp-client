package cn.las.stream;

import org.mp4parser.muxer.samples.SampleList;

/**
 * Created by las on 2017/4/10.
 */
public abstract class MediaStream implements Stream {

    SampleList samples;

    public SampleList getSamples() {
        return samples;
    }

    public void setSamples(SampleList samples) {
        this.samples = samples;
    }
}
