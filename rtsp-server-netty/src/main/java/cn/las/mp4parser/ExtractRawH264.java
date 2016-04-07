package cn.las.mp4parser;


import cn.las.message.NaluHeader;
import org.mp4parser.IsoFile;
import org.mp4parser.boxes.iso14496.part12.TrackBox;
import org.mp4parser.boxes.iso14496.part15.AvcConfigurationBox;
import org.mp4parser.muxer.FileRandomAccessSourceImpl;
import org.mp4parser.muxer.Sample;
import org.mp4parser.muxer.samples.SampleList;
import org.mp4parser.tools.IsoTypeReaderVariable;
import org.mp4parser.tools.Path;

import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/24
 */
public class ExtractRawH264 {
    public static void main(String[] args) throws Exception {
        String path = ExtractRawH264.class.getClassLoader().getResource("").getPath();
        String file = path + "test.mp4";

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

        SampleList sl = new SampleList(trackId, isoFile, new FileRandomAccessSourceImpl(
                new RandomAccessFile(file, "r")));


        FileChannel fc = new FileOutputStream(path + "out.h264").getChannel();
        ByteBuffer separator = ByteBuffer.wrap(new byte[]{0, 0, 0, 1});

        AvcConfigurationBox avcConfigurationBox = (AvcConfigurationBox) Path.getPath(trackBox, "mdia/minf/stbl/stsd/avc1/avcC");
        System.out.println("avcConfigurationBox : ");
        System.out.println(avcConfigurationBox);
        System.out.println("ConfigurationVersion : " + avcConfigurationBox.getConfigurationVersion());
        System.out.println("AvcProfileIndication : " + avcConfigurationBox.getAvcProfileIndication());
        System.out.println("ProfileCompatibility : " + avcConfigurationBox.getProfileCompatibility());
        System.out.println("AvcLevelIndication : "+avcConfigurationBox.getAvcLevelIndication());
        System.out.println("LengthSizeMinusOne : "+avcConfigurationBox.getLengthSizeMinusOne());
        System.out.println("==================================");
        System.out.println("sps : ");
        System.out.println("size : " + avcConfigurationBox.getSequenceParameterSets().size());
        ByteBuffer sps = avcConfigurationBox.getSequenceParameterSets().get(0).slice();
        byte[] byteSPS = sps.array();
        NaluHeader nh = new NaluHeader((byteSPS[0] >> 7), (byteSPS[0] >> 5), (byteSPS[0] & 31));
        System.out.println(nh);
        System.out.println("==================================");
        System.out.println("pps : ");
        System.out.println("size : " + avcConfigurationBox.getPictureParameterSets().size());
        ByteBuffer pps = avcConfigurationBox.getPictureParameterSets().get(0).slice();
        byte[] bytePPS = pps.array();
        nh = new NaluHeader((bytePPS[0] >> 7), (bytePPS[0] >> 5), (bytePPS[0] & 31));
        System.out.println(nh);
        System.out.println("=================================");

        fc.write((ByteBuffer) separator.rewind());
        // Write SPS
        fc.write(avcConfigurationBox.getSequenceParameterSets().get(0));
        // Warning:
        // There might be more than one SPS (I've never seen that but it is possible)

        fc.write((ByteBuffer) separator.rewind());
        // Write PPS
        fc.write(avcConfigurationBox.getPictureParameterSets().get(0));
        // Warning:
        // There might be more than one PPS (I've never seen that but it is possible)

        int lengthSize = ((AvcConfigurationBox) Path.getPath(trackBox, "mdia/minf/stbl/stsd/avc1/avcC")).getLengthSizeMinusOne() + 1;
        for (Sample sample : sl) {
            ByteBuffer bb = sample.asByteBuffer();
            while (bb.remaining() > 0) {
                int length = (int) IsoTypeReaderVariable.read(bb, lengthSize);
                fc.write((ByteBuffer) separator.rewind());
                fc.write((ByteBuffer) bb.slice().limit(length));
                bb.position(bb.position() + length);
            }
        }
        fc.close();
    }
}
