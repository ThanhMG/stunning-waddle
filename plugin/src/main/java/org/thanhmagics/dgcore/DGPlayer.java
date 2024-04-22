package org.thanhmagics.dgcore;

import org.bukkit.boss.BossBar;

import java.io.Serializable;
import java.util.*;

public class DGPlayer implements Serializable {

    public UUID uuid;

    public int mined = 0;

    public List<Multiplier> mul = new ArrayList<>();

    public Map<String, Integer> mined_in_region = new HashMap<>();

    public double multi = 0;

    public Map<EnumUpgradeType, Integer> playerUpgrades = new HashMap<>();

    public int exp = 0,level = 1,f_mul = 0;

    public UUID inv = null;

    public Map<String,Integer> top = new HashMap<>();

    public Long pvpOff = null;

    public int top2 = 0,lma = 0;

    public String lm = "",display;

    public long l = System.currentTimeMillis();

    public DGPlayer(UUID uuid) {
        this.uuid = uuid;
        for (EnumUpgradeType type : EnumUpgradeType.values()) {
            playerUpgrades.put(type,1);
        }
    }

    public void reloadMultiplier() {
        double m = f_mul;
        for (Multiplier multiplier : mul) {
            m += multiplier.mul();
        }
        m += Double.parseDouble(PlayerUpgrade.upgrades.get(EnumUpgradeType.MULTIPLIER).getValue(playerUpgrades.get(EnumUpgradeType.MULTIPLIER)));
        this.multi = m;
    }
}
