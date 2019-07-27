package com.las.message;

/**
 * Created by cn.las on 2016/3/24.
 */
public class RtpHeader {

    private int v = 2;//0011 1111
    private int p = 0;//1101 1111
    private int x = 0;//1110 1111
    private int cc = 0;//1111 0000

    private int m = 1;//0111 1111
    private int pt = 97;//1000 0000

    /**
     * sequnce number
     */
    private int seq;

    private long timestamp;

    private long ssrc;

    public RtpHeader(int v, int p, int x, int cc, int m, int pt, int seq, long timestamp, long ssrc) {
        this.v = v;
        this.p = p;
        this.x = x;
        this.cc = cc;
        this.m = m;
        this.pt = pt;
        this.seq = seq;
        this.timestamp = timestamp;
        this.ssrc = ssrc;
    }

    public byte[] getRtpHeader() {
        int length = 12 + this.cc * 4 + (this.x > 0 ? 4 : 0);
        byte[] b = new byte[length];
        b[0] = (byte) ((v << 6) + (p << 5) + (x << 4) + (cc));
        b[1] = (byte) ((m << 7) + pt);
        b[2] = (byte) (seq >>> 8);
        b[3] = (byte) seq;
        b[4] = (byte) (timestamp >>> 24);
        b[5] = (byte) (timestamp >>> 16);
        b[6] = (byte) (timestamp >>> 8);
        b[7] = (byte) timestamp;
        b[8] = (byte) (ssrc >>> 24);
        b[9] = (byte) (ssrc >>> 16);
        b[10] = (byte) (ssrc >>> 8);
        b[11] = (byte) ssrc;
        for (int i = 0; i < this.cc; i++) {
            b[12 + 4 * i] = 0;
            b[12 + 4 * i + 1] = 0;
            b[12 + 4 * i + 2] = 0;
            b[12 + 4 * i + 3] = 0;
        }
        if (this.x > 0) {
            b[12 + this.cc * 4] = 0;
            b[12 + this.cc * 4 + 1] = 0;
            b[12 + this.cc * 4 + 2] = 0;
            b[12 + this.cc * 4 + 3] = 0;
        }
        return b;
    }

    public int getCc() {
        return cc;
    }

    public void setCc(int cc) {
        this.cc = cc;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public int getPt() {
        return pt;
    }

    public void setPt(int pt) {
        this.pt = pt;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public long getSsrc() {
        return ssrc;
    }

    public void setSsrc(long ssrc) {
        this.ssrc = ssrc;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}
