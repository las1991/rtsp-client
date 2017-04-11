package cn.las.mp4parser;


import cn.las.messageTmp.NaluHeader;
import cn.las.stream.AudioStream;
import cn.las.stream.MediaStream;
import cn.las.stream.VideoStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.log4j.Logger;
import org.mp4parser.Container;
import org.mp4parser.IsoFile;
import org.mp4parser.boxes.iso14496.part12.FileTypeBox;
import org.mp4parser.boxes.iso14496.part12.MovieBox;
import org.mp4parser.boxes.iso14496.part12.MovieHeaderBox;
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

/**
 * Created by las on 2016/3/24.
 */
public class H264Sample {

    static Logger logger = Logger.getLogger(H264Sample.class);

    public static final String PUSH_FILE = "push.file";

    private static IsoFile isoFile;

    private static List<TrackBox> trackBoxes = new ArrayList<TrackBox>();

    public static VideoStream videoStream = new VideoStream();

    public static AudioStream audioStream = new AudioStream();


    static {
        try {
            String file = System.getProperty(PUSH_FILE, "/Users/las/Downloads/zhanlang.mp4");
            System.out.println(file);
            isoFile = new IsoFile(file);

            MovieBox moov = Path.getPath(isoFile, MovieBox.TYPE);

            List<TrackBox> traks = Path.getPaths((Container) moov, TrackBox.TYPE);

            trackBoxes.addAll(traks);
            TrackBox videoTrack = null;
            long videoTrackId = -1;

            TrackBox audioTrack = null;
            long audioTrackId = -1;
            for (TrackBox _trackBox : trackBoxes) {
                if (Path.getPath(_trackBox, "mdia/minf/vmhd") != null) {
                    videoTrackId = _trackBox.getTrackHeaderBox().getTrackId();
                    videoTrack = _trackBox;
                    videoStream.setTimescale(videoTrack.getMediaBox().getMediaHeaderBox().getTimescale());
                    videoStream.setDuration(videoTrack.getMediaBox().getMediaHeaderBox().getDuration() / videoTrack.getMediaBox().getMediaHeaderBox().getTimescale());
                }
                if (Path.getPath(_trackBox, "mdia/minf/smhd") != null) {
                    audioTrackId = _trackBox.getTrackHeaderBox().getTrackId();
                    audioTrack = _trackBox;
                }
            }
            videoStream.setSamples(new SampleList(videoTrackId, isoFile, new FileRandomAccessSourceImpl(
                    new RandomAccessFile(file, "r"))));
            videoStream.setFrame_rate(videoStream.getSamples().size() / videoStream.getDuration());

            audioStream.setSamples(new SampleList(audioTrackId, isoFile, new FileRandomAccessSourceImpl(
                    new RandomAccessFile(file, "r"))));

            AvcConfigurationBox avcConfigurationBox = Path.getPath(videoTrack, "mdia/minf/stbl/stsd/avc1/avcC");
            videoStream.setSampleLengthSize(avcConfigurationBox.getLengthSizeMinusOne() + 1);
            videoStream.setSps(avcConfigurationBox.getSequenceParameterSets().get(0));
            videoStream.setPps(avcConfigurationBox.getPictureParameterSets().get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        H264Sample sample = new H264Sample();
        logger.info(videoStream.getSamples().size());
        logger.info(videoStream.getDuration());
        logger.info(videoStream.getTimescale());
        logger.info(videoStream.getFrame_rate());
    }


}
