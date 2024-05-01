package org.thanhmagics.dgcore.arena;

import org.thanhmagics.dgcore.EnchantType;
import org.thanhmagics.dgcore.RegenMode;
import org.thanhmagics.utils.XMaterial;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArenaData implements Serializable {


    public String id;

    public DGLocation pos1, pos2;

    //  public int regenRegion = 1000 * 60 * 60; //1h

    public Map<DGLocation, String> region = new HashMap<>();

    // public List<CIDropChance> ciDropChances = new ArrayList<>();

    public int levelRequire = 1;

    // public Map<String, Integer> regenCD = new HashMap<>();
    public RegenMode regenMode = RegenMode.PER_BLOCK;

    public Map<String, Integer> expDrop = new HashMap<>();

    public Map<String, Integer> regenCD_per_block = new HashMap<>();

    public int regenCD_time = 1000 * 60 * 60;

    //  public boolean regenCD_if_empty = false;

    public XMaterial block_waiting = XMaterial.BEDROCK;

    public int bbd = 2;

    public Map<String, List<CIDropChance>> ciBlockDropChance = new HashMap<>();

    //   public String default_drop = null;

    public Map<String, Double> miningSpeed = new HashMap<>();

//    public Map<EnumType,Integer> encRequire = new HashMap<>();
//
//    public EnumType enc;

    public Map<EnchantType, Integer> encRequire = new HashMap<>();

    public String permission;
}















