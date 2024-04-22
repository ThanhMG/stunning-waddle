package org.thanhmagics.dgcore;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class PlayerLevel {

//    public static Map<Integer,Integer> value1 = new HashMap<>();
//    public static Map<Integer,Integer> value2 = new HashMap<>();
//
    public static int mine,exp;

    public static void init(FileConfiguration config) {
//        value1.put(1,0);
//        value2.put(1,0);
//        for (String key : config.getConfigurationSection("level").getKeys(false)) {
//            int level = (config.getInt("level." + key + ".level"));
//            int mine = config.getInt("level." + key + ".mine");
//            int exp = config.getInt("level." + key + ".exp");
//            PlayerLevel.value1.put(level,mine);
//            PlayerLevel.value2.put(level,exp);
//        }
        mine = config.getInt("level.mine");
        exp = config.getInt("level.exp");
    }

    public static int getMine(int level) {
        return mine * level;
    }

    public static int getExp(int level) {
        return exp * level;
    }

}
