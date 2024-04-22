package org.thanhmagics.dgcore;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.thanhmagics.DGCore;
import org.thanhmagics.utils.Utils;
import org.thanhmagics.utils.XMaterial;

import java.io.Serializable;
import java.util.*;

public class PlayerUpgrade implements Serializable {

    public static Map<EnumUpgradeType,PlayerUpgrade> upgrades = new HashMap<>();

    public String filePath;
//    public Map<Integer,Integer> cost = new HashMap<>();
//
//    public Map<Integer,Double> value = new HashMap<>();

 //   public Map<EnumUpgradeType,Integer> ct = new HashMap<>();

    public int ct;
    public double ve;

    public String name;

 //   public Map<EnumUpgradeType,Double> ve = new HashMap<>();

    public FileConfiguration config;

    public EnumUpgradeType type;

    public PlayerUpgrade(String filePath, FileConfiguration config,EnumUpgradeType type) {
        this.filePath = filePath;
        this.config = config;
        this.type = type;
    }

    public void init() {
        upgrades.put(type,this);
        ct = config.getInt(type.name().toLowerCase() + ".cost");
        ve = config.getDouble(type.name().toLowerCase() + ".value");
        name = config.getString(type.name().toLowerCase() + ".name");
//        for (String s : config.getConfigurationSection(filePath).getKeys(false)) {
//            int level = Integer.parseInt(config.getString(filePath + "." + s + ".level"));
//            if (cost.containsKey(level)) throw new RuntimeException("ERROR AT: upgrade.yml (" + filePath + "." + s + ".level" + ") level bị lập lại");
//            int cost = Integer.parseInt( config.getString(filePath + "." + s + ".cost"));
//            double value = Double.parseDouble(config.getString(filePath + "." + s + ".value"));
//            this.cost.put(level,cost);
//            this.value.put(level,value);
//            for (DGPlayer player : DGCore.getInstance().dataSerialize.player.values()) {
//                if (player.playerUpgrades.containsKey(type)) continue;
//                player.playerUpgrades.put(type,1);
//            }
//        }
    }

    public int getCost(int level) {
        return ct * level;
    }

    public String getValue(int level) {
        double value = ve * level;
        return String.format("%.1f",value);
    }

    public String replacer(DGPlayer player,String input) {
        return replacer(player,List.of(input)).get(0);
    }

    public List<String> replacer(DGPlayer player,List<String> list) {
        List<String> rs = new ArrayList<>();
        int lvl = player.playerUpgrades.get(type);
//        boolean max_lvl = cost.size() <= lvl;
//        boolean b = false;
        for (String str : list) {
//            if (max_lvl) {
//                if (str.contains("{max_lvl_lore}")) {
//                    b = true;
//                    continue;
//                }
//            }
//            if (b) {
//                rs.addAll(DGCore.getInstance().upgrade_gui.config.getStringList("content."+type.name().toLowerCase() + ".max_lvl_lore"));
//                break;
//            }
            if (str.contains("{max_lvl_lore}")) continue;
            rs.add(str.replace("{level}",String.valueOf(lvl))
                    .replace("{level_next}",String.valueOf((Integer.sum(lvl,1))))
                    .replace("{level_cost}",String.valueOf(getCost(Integer.sum(lvl,1))))
                    .replace("{level_value}",String.valueOf(getValue(lvl)))
                    .replace("{level_nextValue}",String.valueOf(getValue(Integer.sum(lvl,1)))));
        }
        return rs;
    }

    public static ItemStack loreReplacer(ItemStack is) {
        if (is == null || is.getItemMeta() == null || is.getItemMeta().getLore() == null) return is;
        List<String> lore = new ArrayList<>(is.getItemMeta().getLore());
        List<Integer> replaced = new ArrayList<>();
        int i = 0;
        String string = DGCore.getInstance().nmsManager.nbtStorage().get(is,"DGReplacer");
//        if (string != null && string.length() > 0) {
//            int integer = Integer.parseInt(string.split("▸")[0]);
//            if (string.split("▸")[1].equals(is.getItemMeta().getLore().get(integer))) {
//                String v = string.split("▸")[1];
//                String value = v.split("\\{core_itemlvl:")[1].split("}")[0];
//                if (value.contains(":")) {
//                    String first = value.split(":")[0];
//                    String second = value.split(":")[1];
//                    if (first.equalsIgnoreCase("DROP")) {
//                        if (DGCore.getInstance().arenas.containsKey(second)) {
//                            String s = DGCore.getInstance().nmsManager.nbtStorage().get(is,"dgcore:DROP:" + second);
//                            String s1 = string.split("▸")[2].replace("{core_itemlvl:DROP:" + second + "}",
//                                    s != null ? s : "0");
//                            lore.set(integer,s1);
//                            replaced.add(integer);
//                            is = DGCore.getInstance().nmsManager.nbtStorage().put(is,"DGReplacer",integer + "▸" + v + "▸" + s1 + "▸" + string.split("▸")[3]);
//                        }
//                    } else if (first.equalsIgnoreCase("MULTIPLE")) {
//                        String s = DGCore.getInstance().nmsManager.nbtStorage().get(is,"dgcore:MULTIPLE:" + second);
//                        String s1 = string.split("▸")[2].replace("{core_itemlvl:MULTIPLE:" + second + "}",
//                                s != null ? s : "0");
//                        lore.set(integer,s1);
//                        replaced.add(integer);
//                        is = DGCore.getInstance().nmsManager.nbtStorage().put(is,"DGReplacer",integer + "▸" + v + "▸" + s1 + "▸" + string.split("▸")[3]);
//                    }
//                } else {
//                    if (value.split(":")[0].equalsIgnoreCase("DROP")) {
//                        String s = DGCore.getInstance().nmsManager.nbtStorage().get(is,"dgcore:DROP:");
//                        String s1 = string.split("▸")[2].replace("{core_itemlvl:DROP}",
//                                s != null ? s : "0");
//                        lore.set(integer,s1);
//                        replaced.add(integer);
//                        is = DGCore.getInstance().nmsManager.nbtStorage().put(is,"DGReplacer",integer + "▸" + v + "▸" + s1 + "▸" + string.split("▸")[3]);
//                    } else if (value.split(":")[0].equalsIgnoreCase("MULTIPLE")) {
//                        String s = DGCore.getInstance().nmsManager.nbtStorage().get(is,"dgcore:MULTIPLE:");
//                        String s1 = string.split("▸")[2].replace("{core_itemlvl:MULTIPLE}",
//                                s != null ? s : "0");
//                        lore.set(integer,s1);
//                        replaced.add(integer);
//                        is = DGCore.getInstance().nmsManager.nbtStorage().put(is,"DGReplacer",integer + "▸" + v + "▸" + s1 + "▸" + string.split("▸")[3]);
//                    }
//                }
//            } else {
//                DGCore.getInstance().nmsManager.nbtStorage().remove(is,"DGReplacer");
//            }
//        }
        if (string != null && string.length() > 0) {
            int j = Integer.parseInt(string.split("▸")[0]);
            String s1 = string.split("▸")[1];
            String s2 = string.split("▸")[2];
            String s3 = string.split("▸")[3];
            if (is.getItemMeta().getLore().get(j).equals(s1)) {
                String s4 = s3.split("\\{core_itemlvl:")[1];
                if (s4.contains(":")) {
                    String first = s4.split(":")[0];
                    String second = s4.split(":")[1];
                    if (first.equalsIgnoreCase("DROP")) {
                        if (DGCore.getInstance().arenas.containsKey(second)) {
                            String s5 = DGCore.getInstance().nmsManager.nbtStorage().get(is, "dgcore:DROP:" + second);
                            String s6 = s2.replace("{core_itemlvl:DROP:" + second + "}", s5 != null ? (s5.length() > 0 ? s5 : "0") : "0");
                            lore.set(j, s6);
                            replaced.add(j);
                            is = DGCore.getInstance().nmsManager.nbtStorage().put(is, "DGReplacer", j + "▸" + s6 + "▸" + s2 + "▸" + s3);
                        }
                    } else if (first.equalsIgnoreCase("MULTIPLE")) {
                        Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(second.toUpperCase());
                        if (xMaterial.isPresent()) {
                            String s5 = DGCore.getInstance().nmsManager.nbtStorage().get(is, "dgcore:MULTIPLE:" + second);
                            String s6 = s2.replace("{core_itemlvl:MULTIPLE:" + second + "}", s5 != null ? (s5.length() > 0 ? s5 : "0") : "0");
                            lore.set(j, s6);
                            replaced.add(j);
                            is = DGCore.getInstance().nmsManager.nbtStorage().put(is, "DGReplacer", j + "▸" + s6 + "▸" + s2 + "▸" + s3);
                        }
                    }
                } else {
                    if (s4.equalsIgnoreCase("DROP")) {
                        String s5 = DGCore.getInstance().nmsManager.nbtStorage().get(is, "dgcore:DROP:");
                        String s6 = s2.replace("{core_itemlvl:DROP}", s5 != null ? (s5.length() > 0 ? s5 : "0") : "0");
                        lore.set(j, s6);
                        replaced.add(j);
                        is = DGCore.getInstance().nmsManager.nbtStorage().put(is, "DGReplacer", j + "▸" + s6 + "▸" + s2 + "▸" + s3);
                    } else if (s4.equalsIgnoreCase("MULTIPLE")) {
                        String s5 = DGCore.getInstance().nmsManager.nbtStorage().get(is, "dgcore:MULTIPLE:");
                        String s6 = s2.replace("{core_itemlvl:MULTIPLE}", s5 != null ? (s5.length() > 0 ? s5 : "0") : "0");
                        lore.set(j, s6);
                        replaced.add(j);
                        is = DGCore.getInstance().nmsManager.nbtStorage().put(is, "DGReplacer", j + "▸" + s6 + "▸" + s2 + "▸" + s3);
                    }
                }
            } else {
                DGCore.getInstance().nmsManager.nbtStorage().remove(is,"DGReplacer");
            }
        }
        for (String str : is.getItemMeta().getLore()) {
            if (replaced.contains(i)) continue;
            if (str.contains("{core_itemlvl:")) {
                  is = DGCore.getInstance().nmsManager.nbtStorage().put(is,"DGReplacer", i + "▸" + str + "▸" + str + "▸" +"{core_itemlvl:"
                        + str.split("\\{core_itemlvl:")[1].split("}")[0]);
            }
            i++;
        }
//        List<String> ol = is.getItemMeta().getLore();
//        for (int i = 0; i < ol.size(); i++) {
//            if (!ol.get(i).equals(lore.get(i))) {
//                Bukkit.getScheduler().runTask(DGCore.getInstance(),() -> is.getItemMeta().setLore(lore));
//                break;
//            }
//        }
        ItemMeta meta = is.getItemMeta();
        meta.setLore(lore);
        is.setItemMeta(meta);
        return is;
    }
}
