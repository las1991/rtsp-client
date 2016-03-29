package cn.las.rtp;

import cn.las.client.ClientPush;
import cn.las.message.*;
import cn.las.mp4parser.H264Sample;
import cn.las.util.ByteUtil;
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

    public static List<RtpPackage> getRtpPackages(Sample sample, ClientPush clientPush) {
        List<RtpPackage> packages = new ArrayList<>();

        ByteBuffer nal = sample.asByteBuffer();
        byte[] a = nal.array();
        System.out.println(a.length);

        while (nal.remaining()>0){
            int length = (int) IsoTypeReaderVariable.read(nal, H264Sample.lengthSize);
            byte[] bytes = new byte[length];
            nal.get(bytes, 0, length);
            packages.addAll(createPackages(bytes,clientPush));
        }
        clientPush.setTimestamp(clientPush.getTimestamp()+3600);
        return packages;
    }

    /**
     * 判断是否为0x00 00 01,如果是返回1
     *
     * @param Buf
     * @return
     */
    private static int FindStartCode2(byte[] Buf) {
        if (Buf[0] != 0 || Buf[1] != 0 || Buf[2] != 1) return 0;
        else return 1;
    }

    /**
     * 判断是否为0x00 00 00 01,如果是返回1
     *
     * @param Buf
     * @return
     */
    private static int FindStartCode3(byte[] Buf) {
        if (Buf[0] != 0 || Buf[1] != 0 || Buf[2] != 0 || Buf[3] != 1) return 0;
        else return 1;
    }

    private static List<RtpPackage> createPackages(byte[] bytes,ClientPush clientPush){
        List<RtpPackage> packages=new ArrayList<>();
        int seq=clientPush.getSeq();
        long timestamp=clientPush.getTimestamp();
        int length=bytes.length;
        NaluHeader naluHeader = new NaluHeader((bytes[0] >> 7), (bytes[0] >> 5), (bytes[0] & 31));
        System.out.println(naluHeader);
        if ((length-1) > MTU) {
            int k = 0, last = 0;
            k = (bytes.length-1) / MTU;//需要k个1400字节的RTP包，这里为什么不加1呢？因为是从0开始计数的。
            last = (bytes.length-1) % MTU;//最后一个RTP包的需要装载的字节数
            int t = 0;//用于指示当前发送的是第几个分片RTP包
            while (t <= k) {
                RtpBody rtpBody = new RtpBody();
                FuHeader fuHeader;
                FuIndicator fuIndicator = new FuIndicator(naluHeader.getF(), naluHeader.getNri(), 28);
                RtpHeader rtpHeader;
                if (t < k) {
                    rtpHeader=new RtpHeader(2, 0, 0, 0, 0, 97,
                            seq++, timestamp, ByteUtil.htonl(10));
                    rtpBody.setData(Arrays.copyOfRange(bytes, 1+t*MTU, 1+t*MTU+MTU));
                    if (t == 0) fuHeader = new FuHeader(0, 0, 1, naluHeader.getType());
                    else fuHeader = new FuHeader(0, 0, 0, naluHeader.getType());
                } else {
                    fuHeader = new FuHeader(1, 0, 0, naluHeader.getType());
                    rtpHeader=new RtpHeader(2, 0, 0, 0, 1, 97,
                            seq++, timestamp, ByteUtil.htonl(10));
                    rtpBody.setData(Arrays.copyOfRange(bytes, 1+t*MTU, 1+t*MTU+last));
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


    public static void main(String[] args) {
//        getRtpPackages(H264Sample.samples.get(0));
        ClientPush clientPush=new ClientPush("");
        for (Sample sample : H264Sample.samples) {
            getRtpPackages(sample,clientPush);
        }
    }
}
