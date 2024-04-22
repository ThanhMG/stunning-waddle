package org.thanhmagics;

public abstract class PPIBlockDig {

    public Object blockPoss;

    public int x,y,z;

    public String type;

    public Object packet;

    public PPIBlockDig(Object packet) {
        this.packet = packet;
    }

    public abstract void init();

}
