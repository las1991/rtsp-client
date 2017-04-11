package cn.las.stream;

/**
 * Created by las on 2017/4/10.
 */
public class AudioStream extends MediaStream {

    long sample_rate;

    @Override
    public int getSSRC() {
        return 0;
    }
}
