package cn.las.encoder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by las on 2017/6/6.
 */
public class H264Encoder {

    public static void main(String[] args) {
        try {
            RandomAccessFile srcFile = new RandomAccessFile(new File("/Users/las/Desktop/yuanshi.h264"), "r");
            FileChannel srcFileChannel = srcFile.getChannel();
            ByteBuffer src = ByteBuffer.allocate((int) srcFile.length());
            srcFileChannel.read(src);
            src.flip();
            srcFileChannel.close();
            RandomAccessFile dstFile = new RandomAccessFile(new File("/Users/las/Desktop/dst.h264"), "rw");
            FileChannel dstFileChannel = dstFile.getChannel();
            ByteBuffer dst = test(src);
            dstFileChannel.write(dst);
            dstFileChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static ByteBuffer test(ByteBuffer src) {
        ByteBuffer dst = ByteBuffer.allocate(src.capacity());
        dst.put(new byte[]{0x00, 0x00, 0x00, 0x01});
        while (src.hasRemaining()) {
            int index = getStartCode(src);
            int nal = src.get(src.position()) & 0x1f;
            System.out.println("nal type:" + nal);
            while (src.position() <= index && src.hasRemaining()) {
                if (nal == 0) {
                    System.out.printf("0x%x,", src.get());
                } else {
                    dst.put(src.get());
                }
            }
            System.out.println("");
        }
        dst.flip();
        return dst;
    }

    private static int getStartCode(ByteBuffer src) {
        if (!src.hasRemaining())
            return src.position();

        int val = 0xffffffff;
        int i = src.position();
        while (i < src.limit()) {
            val <<= 8;
            val |= (src.get(i) & 0xff);
            if ((val & 0xffffff) == 1) {
                return i;
            }
            i++;
        }
        return src.limit();
    }
}
