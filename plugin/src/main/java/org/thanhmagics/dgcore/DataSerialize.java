package org.thanhmagics.dgcore;

import org.thanhmagics.dgcore.arena.ArenaData;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DataSerialize implements Serializable {

    public Map<String, DGPlayer> player = new HashMap<>();

    //public Map<String,Arena> arena = new HashMap<>();

    public Map<String, ArenaData> arena = new HashMap<>();

    public String pvp_itemstack = null;

}
