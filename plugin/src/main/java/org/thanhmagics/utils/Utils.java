package org.thanhmagics.utils;

import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.thanhmagics.dgcore.arena.DGLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Utils {
    public static int random(int min, int max) {
        return new Random().nextInt(max - min) + min;
    }

    public static String applyColor(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> applyColor(List<String> s) {
        List<String> rs = new ArrayList<>();
        for (String ss : s)
            rs.add(applyColor(ss));
        return rs;
    }

    public static String formatMilisec(int ms) {
        try {
            int h = ms / 3600000;
            int m = (ms % 3600000) / 60000;
            return h + "h" + m + "m";
        } catch (Exception e) {
            return "0h0m";
        }
    }

    public static List<String> stringList(List<String> list, int limit) {
        List<String> rs = new ArrayList<>();
        int line = 0;
        StringBuilder sb = new StringBuilder();
        for (String str : list) {
            if (line + str.length() >= limit) {
                sb.deleteCharAt(sb.length() - 1);
                rs.add(sb.toString());
                sb = new StringBuilder();
                line = 0;
            } else {
                sb.append(applyColor(str)).append("&f, ");
                line += str.length();
            }
        }

        return rs;
    }


    public static boolean insideBox(DGLocation point, DGLocation l1, DGLocation l2, int deviation) {
        if (!l1.w.equals(l2.w)) return false;
        if (!point.w.equals(l1.w)) return false;
        return insideBox(point.x, point.y, point.z, l1.x, l1.y, l1.z, l2.x, l2.y, l2.z, deviation);
    }

    public static boolean insideBox(int x1, int y1, int z1, int x2, int y2, int z2, int x3, int y3, int z3, int deviation) {
        int maxX = Math.max(x2, x3) + deviation, minX = Math.min(x2, x3) - deviation;
        int maxY = Math.max(y2, y3) + deviation, minY = Math.min(y2, y3) - deviation;
        int maxZ = Math.max(z2, z3) + deviation, minZ = Math.min(z2, z3) - deviation;
        if (x1 >= minX && x1 <= maxX) {
            if (y1 >= minY && y1 <= maxY) {
                if (z1 >= minZ && z1 <= maxZ) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Object getKeyByValue(Map<Object, Object> map, Object value) {
        for (Object key : map.keySet())
            if (map.get(key).equals(value))
                return key;
        return null;
    }

    public static boolean isInvFull(Inventory inventory) {
        ItemStack[] is = inventory.getContents();
        for (int i = 0; i < is.length; i++) {
            if (is[i] == null)
                return false;
        }
        return true;
    }

    public static long stringToMs(String input) {
        long l1 = Long.parseLong(input.split("h")[0]);
        long l2 = Long.parseLong(input.split("m")[0].split("h")[1]);
        return (l1 * 3600000) + (l2 * 60000);
    }

}
