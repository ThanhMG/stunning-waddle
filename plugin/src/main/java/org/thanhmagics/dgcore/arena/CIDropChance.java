package org.thanhmagics.dgcore.arena;

import java.io.Serializable;

public class CIDropChance implements Serializable {
    public String data;

    public Double chance;

 //   public boolean default_drop = false;

    public CIDropChance(String data, Double chance) {
        this.data = data;
        this.chance = chance;
    }
}
