package com.las.messageTmp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;

/**
 * Created by las on 2017/2/24.
 */
public class NaluHeader extends DefaultByteBufHolder {

    public NaluHeader(ByteBuf data) {
        super(data);
    }

    public int getF() {
        return content().getUnsignedByte(0) >> 7;//1000 0000
    }


    public int getNri() {
        return content().getUnsignedByte(0) & 0x60;//0110 0000
    }


    public int getType() {
        return content().getUnsignedByte(0) & 0x1f;//0001 1111
    }

    @Override
    public String toString() {
        return "NaluHeader{" +
                "f=" + getF() +
                ", nri=" + getNri() +
                ", type=" + getType() +
                '}';
    }

}
