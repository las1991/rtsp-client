package cn.las.message;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/25
 */
public class RtpBody {

    private NaluHeader naluHeader;

    private FuIndicator fuIndicator;

    private FuHeader fuHeader;

    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public FuHeader getFuHeader() {
        return fuHeader;
    }

    public void setFuHeader(FuHeader fuHeader) {
        this.fuHeader = fuHeader;
    }

    public FuIndicator getFuIndicator() {
        return fuIndicator;
    }

    public void setFuIndicator(FuIndicator fuIndicator) {
        this.fuIndicator = fuIndicator;
    }

    public NaluHeader getNaluHeader() {
        return naluHeader;
    }

    public void setNaluHeader(NaluHeader naluHeader) {
        this.naluHeader = naluHeader;
    }
}
