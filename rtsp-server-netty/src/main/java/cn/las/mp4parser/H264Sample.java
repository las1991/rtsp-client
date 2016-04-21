package cn.las.mp4parser;

import org.mp4parser.IsoFile;
import org.mp4parser.boxes.iso14496.part12.TrackBox;
import org.mp4parser.boxes.iso14496.part15.AvcConfigurationBox;
import org.mp4parser.muxer.FileRandomAccessSourceImpl;
import org.mp4parser.muxer.Sample;
import org.mp4parser.muxer.samples.SampleList;
import org.mp4parser.tools.Path;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by las on 2016/3/24.
 */
public class H264Sample {

    public final static ByteBuffer SEPARATOR = ByteBuffer.wrap(new byte[]{0, 0, 0, 1});

    public static IsoFile isoFile;

    public static SampleList samples;

    public static List<TrackBox> trackBoxes = new ArrayList<TrackBox>();

    public static int lengthSize;

    public static ByteBuffer sps;

    public static ByteBuffer pps;

    static {
        try {
//            String file=H264Sample.class.getClassLoader().getResource("").getPath()+"cab9a9bc-235b-433e-a033-fcf2b26220d5.mp4";
            String file = H264Sample.class.getClassLoader().getResource("").getPath() + "test1.mp4";
            isoFile = new IsoFile(file);
            trackBoxes.add((TrackBox) Path.getPath(isoFile, "moov/trak/"));
            long trackId = -1;
            TrackBox trackBox = null;
            for (TrackBox _trackBox : trackBoxes) {
                if (Path.getPath(_trackBox, "mdia/minf/stbl/stsd/avc1") != null) {
                    trackId = _trackBox.getTrackHeaderBox().getTrackId();
                    trackBox = _trackBox;
                }
            }
            samples = new SampleList(trackId, isoFile, new FileRandomAccessSourceImpl(
                    new RandomAccessFile(file, "r")));
            lengthSize = ((AvcConfigurationBox) Path.getPath(trackBox, "mdia/minf/stbl/stsd/avc1/avcC")).getLengthSizeMinusOne() + 1;
            sps = ((AvcConfigurationBox) Path.getPath(trackBox, "mdia/minf/stbl/stsd/avc1/avcC")).getSequenceParameterSets().get(0);
            pps = ((AvcConfigurationBox) Path.getPath(trackBox, "mdia/minf/stbl/stsd/avc1/avcC")).getPictureParameterSets().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AtomicInteger sampleIndex = new AtomicInteger(0);

    public Sample getSample() {
        Integer index;
        if (sampleIndex.get() > samples.size()-1)
            index = sampleIndex.getAndSet(0);
        else
            index = sampleIndex.getAndAdd(1);

        if (index < samples.size()) {
            return samples.get(index);
        }
        return null;
    }

}
