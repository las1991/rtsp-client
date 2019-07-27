package com.las.message;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/25
 */
public class FuHeader {

    private int s;
    private int e;
    private int r = 0;
    private int type;

    public byte getFuHeader() {
        return (byte) ((s << 7) + (e << 6) + (r << 5) + type);
    }

    public FuHeader(int e, int r, int s, int type) {
        this.e = e;
        this.r = r;
        this.s = s;
        this.type = type;
    }

    public int getE() {
        return e;
    }

    public void setE(int e) {
        this.e = e;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
