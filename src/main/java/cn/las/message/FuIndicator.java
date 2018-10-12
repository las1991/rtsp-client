package cn.las.message;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/25
 */
public class FuIndicator {

    private int f;
    private int nri;
    private int type;

    public byte getFuIndicator() {
        return (byte) ((f << 7) + (nri << 5) + type);
    }

    public FuIndicator(int f, int nri, int type) {
        this.f = f;
        this.nri = nri;
        this.type = type;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getNri() {
        return nri;
    }

    public void setNri(int nri) {
        this.nri = nri;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
