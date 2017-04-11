package cn.las.rtp;

import cn.las.client.AbstractClient;
import cn.las.client.ClientPush;
import cn.las.message.*;
import cn.las.mp4parser.H264Sample;
import cn.las.util.ByteUtil;
import io.netty.buffer.ByteBufUtil;
import org.mp4parser.muxer.Sample;
import org.mp4parser.tools.IsoTypeReaderVariable;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by las on 2016/3/24.
 */
public class RtpPacketizer {

    private final static int MTU = 1400;

    public static List<RtpPackage> getRtpPackages(AbstractClient.ClientSession clientPush) {
        Sample sample = clientPush.getVideoSample(H264Sample.videoStream);
        List<RtpPackage> packages = new ArrayList<>();
        if (sample == null) {
            return packages;
        }

//        clientPush.setTimestamp(clientPush.getTimestamp() + H264Sample.videoStream.getTimescale() / H264Sample.videoStream.getFrame_rate());
        boolean hasSps = false;//type=7
        boolean hasPps = false;//type=8
        ByteBuffer nal = sample.asByteBuffer();
        while (nal.remaining() > 0) {
            int length = (int) IsoTypeReaderVariable.read(nal, H264Sample.videoStream.getSampleLengthSize());
            byte[] bytes = new byte[length];
            nal.get(bytes, 0, length);
            NaluHeader naluHeader = new NaluHeader((bytes[0] >> 7), (bytes[0] >> 5), (bytes[0] & 31));
            if (naluHeader.getType() == 7) {
                hasSps = true;
            }
            if (naluHeader.getType() == 8) {
                hasPps = true;
            }
            if (naluHeader.getType() == 5) {
                if (!hasPps) {
                    byte[] b = H264Sample.videoStream.getPps().array();
                    NaluHeader nh = new NaluHeader((b[0] >> 7), (b[0] >> 5), (b[0] & 31));
                    packages.addAll(createPackages(b, clientPush, nh));
                }
                if (!hasSps) {
                    byte[] b = H264Sample.videoStream.getSps().array();
                    NaluHeader nh = new NaluHeader((b[0] >> 7), (b[0] >> 5), (b[0] & 31));
                    packages.addAll(createPackages(b, clientPush, nh));
                }
            }
            packages.addAll(createPackages(bytes, clientPush, naluHeader));
        }
        return packages;
    }

    private static List<RtpPackage> createPackages(byte[] bytes, AbstractClient.ClientSession clientPush, NaluHeader naluHeader) {
        List<RtpPackage> packages = new ArrayList<>();
        int seq = clientPush.getSeq();
        long timestamp = clientPush.getTimestamp();
        int length = bytes.length;
        if ((length - 1) > MTU) {
            int k = 0, last = 0;
            k = (bytes.length - 1) / MTU;//需要k个1400字节的RTP包，这里为什么不加1呢？因为是从0开始计数的。
            last = (bytes.length - 1) % MTU;//最后一个RTP包的需要装载的字节数
            int t = 0;//用于指示当前发送的是第几个分片RTP包
            while (t <= k) {
                RtpBody rtpBody = new RtpBody();
                FuHeader fuHeader;
                FuIndicator fuIndicator = new FuIndicator(naluHeader.getF(), naluHeader.getNri(), 28);
                RtpHeader rtpHeader;
                if (t < k) {
                    rtpHeader = new RtpHeader(2, 0, 1, 0, 0, 97,
                            seq++, timestamp, ByteUtil.htonl(10));
                    rtpBody.setData(Arrays.copyOfRange(bytes, 1 + t * MTU, 1 + t * MTU + MTU));
                    if (t == 0) fuHeader = new FuHeader(0, 0, 1, naluHeader.getType());
                    else fuHeader = new FuHeader(0, 0, 0, naluHeader.getType());
                } else {
                    fuHeader = new FuHeader(1, 0, 0, naluHeader.getType());
                    rtpHeader = new RtpHeader(2, 0, 1, 0, 1, 97,
                            seq++, timestamp, ByteUtil.htonl(10));
                    rtpBody.setData(Arrays.copyOfRange(bytes, 1 + t * MTU, 1 + t * MTU + last));
                }
                rtpBody.setFuHeader(fuHeader);
                rtpBody.setFuIndicator(fuIndicator);
                packages.add(new RtpPackage(rtpHeader, rtpBody));
                t++;
            }
        } else {
            RtpHeader rtpHeader = new RtpHeader(2, 0, 0, 0, 1, 97,
                    seq++, timestamp, ByteUtil.htonl(10));
            RtpBody rtpBody = new RtpBody();
            rtpBody.setNaluHeader(naluHeader);
            rtpBody.setData(Arrays.copyOfRange(bytes, 1, bytes.length));
            packages.add(new RtpPackage(rtpHeader, rtpBody));
        }
        clientPush.setSeq(seq);
        return packages;
    }

}
