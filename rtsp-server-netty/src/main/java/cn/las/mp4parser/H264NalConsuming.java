package cn.las.mp4parser;

import org.mp4parser.IsoFile;
import org.mp4parser.boxes.iso14496.part12.TrackBox;
import org.mp4parser.muxer.FileRandomAccessSourceImpl;
import org.mp4parser.muxer.Sample;
import org.mp4parser.muxer.samples.SampleList;
import org.mp4parser.streaming.input.h264.H264NalConsumingTrack;
import org.mp4parser.tools.Path;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;


/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/25
 */
public class H264NalConsuming extends H264NalConsumingTrack {

    public H264NalConsuming() throws IOException {
        String file="C:\\Users\\andy\\Desktop\\1365070268951.mp4";
        IsoFile isoFile = new IsoFile(file);
        List<TrackBox> trackBoxes = new ArrayList<TrackBox>();
        trackBoxes.add((TrackBox) Path.getPath(isoFile, "moov/trak/"));
        long trackId = -1;
        TrackBox trackBox = null;
        for (TrackBox _trackBox : trackBoxes) {
            if (Path.getPath(_trackBox, "mdia/minf/stbl/stsd/avc1") != null) {
                trackId = _trackBox.getTrackHeaderBox().getTrackId();
                trackBox = _trackBox;
            }
        }
        SampleList samples = new SampleList(trackId, isoFile, new FileRandomAccessSourceImpl(
                new RandomAccessFile(file, "r")));
        for (Sample sample:samples){
            super.consumeNal(sample.asByteBuffer());
        }
    }

    public static void main(String[] args) {
        try {
            H264NalConsuming mp4=new H264NalConsuming();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
