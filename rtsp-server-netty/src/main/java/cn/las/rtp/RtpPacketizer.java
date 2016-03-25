package cn.las.rtp;

import cn.las.message.NaluHeader;
import cn.las.message.RtpPackage;
import cn.las.mp4parser.H264Sample;
import org.mp4parser.muxer.Sample;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by las on 2016/3/24.
 */
public class RtpPacketizer {

    public static List<RtpPackage> getRtpPackages(Sample sample) {
        List<RtpPackage> packages = new ArrayList<>();
        ByteBuffer nal = sample.asByteBuffer();
        byte[] bytes = nal.array();
        byte type;
        if (bytes[3] == 0x01) {
            type = bytes[4];
        } else {
            type = bytes[3];
        }
        NaluHeader naluHeader = new NaluHeader((type >> 7), (type >> 5), (type & 31));
        System.out.println(naluHeader + "," + bytes.length);
        return packages;
    }

    public static void main(String[] args) {
//        getRtpPackages(H264Sample.samples.get(0));
        for (Sample sample : H264Sample.samples) {
            getRtpPackages(sample);
        }
    }

    private int FindStartCode2(byte[] Buf) {
        if (Buf[0] != 0 || Buf[1] != 0 || Buf[2] != 1) return 0; //判断是否为0x000001,如果是返回1
        else return 1;
    }

    private int FindStartCode3(byte[] Buf) {
        if (Buf[0] != 0 || Buf[1] != 0 || Buf[2] != 0 || Buf[3] != 1) return 0;//判断是否为0x00000001,如果是返回1
        else return 1;
    }
}
