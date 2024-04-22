package org.thanhmagics.dgcore;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.thanhmagics.DGCore;
import org.thanhmagics.utils.Utils;

import java.util.Arrays;

public class PAPIHook extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "core";
    }

    @Override
    public String getAuthor() {
        return "ThanhMagics";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }


    @Override
    public String onRequest(OfflinePlayer player,String params) {
        DGPlayer dgPlayer = DGCore.getInstance().dataSerialize.player.get(player.getUniqueId().toString());
        if (params.equalsIgnoreCase("rank_boost")) {
            return String.valueOf(dgPlayer.display);
        }
        if (params.equalsIgnoreCase("player_level")) {
            return String.valueOf(dgPlayer.level);
        } else if (params.equalsIgnoreCase("player_exp")) {
            return String.valueOf(dgPlayer.exp);
        } else if (params.equalsIgnoreCase("nextLvl_exp")) {
            int lvl = dgPlayer.level;
            int integer = PlayerLevel.getExp(Integer.sum(lvl,1));
            return String.valueOf((integer - dgPlayer.exp));
        } else if (params.equalsIgnoreCase("nextLvl_block")) {
            int lvl = dgPlayer.level;
            int integer = PlayerLevel.getMine(Integer.sum(lvl,1));
            return String.valueOf((integer - dgPlayer.mined));
        } else {
            if (params.equalsIgnoreCase("multiplier")) {
                return String.valueOf(dgPlayer.multi);
            } else if (params.equalsIgnoreCase("timeleft")) {
                int t = 0;
                for (Multiplier multiplier : dgPlayer.mul) {
                    t += multiplier.end() - System.currentTimeMillis();
                }
                return Utils.formatMilisec(t);
            } else if (params.equalsIgnoreCase("mined")) {
                return String.valueOf(dgPlayer.mined);
            } else if (params.contains(":")) {
                if (params.split(":")[0].equalsIgnoreCase("mined")) {
                    String arena = params.split(":")[1];
                    if (DGCore.getInstance().arenas.containsKey(arena.toLowerCase())) {
                        return String.valueOf(dgPlayer.mined_in_region.get(arena.toLowerCase()));
                    } else {
                        return "NULL ARENA";
                    }
                } else if (params.split(":")[0].equalsIgnoreCase("player_top")) {
                    String[] strings = params.split(":");
//                    return Arena.top_Player.containsKey(top) ?
//                    Bukkit.getOfflinePlayer(Arena.top_Player.get(top).uuid).getName() : " - ";
                    String region = strings[1].toLowerCase();
                    if (DGCore.getInstance().arenas.containsKey(region)) {
                        return String.valueOf(dgPlayer.top.get(region));
                    } else {
                        return "NULL ARENA";
                    }

                } else if (params.split(":")[0].equalsIgnoreCase("top")) {
                    String[] strings = params.split(":");
                    if (strings.length == 2) {
                        int i = Integer.parseInt(strings[1]);
                        return Arena.top_Player.containsKey(i) ?
                                Bukkit.getOfflinePlayer(Arena.top_Player.get(i).uuid).getName() : " - ";
                    } else if (strings.length == 3) {
                        int i = Integer.parseInt(strings[1]);
                        String region = strings[2];
                        if (DGCore.getInstance().arenas.containsKey(region.toLowerCase())) {
                            Arena arena = DGCore.getInstance().arenas.get(region.toLowerCase());
                            return arena.topPlayer.containsKey(i) ? Bukkit.getOfflinePlayer(arena.topPlayer.get(i).uuid).getName() : " - ";
                        } else {
                            return "NULL_ARENA";
                        }
                    }
                } else if (params.split(":")[0].equalsIgnoreCase("require_level")) {
                    String region = params.split(":")[1].toLowerCase();
                    if (DGCore.getInstance().arenas.containsKey(region)) {
                        return String.valueOf(DGCore.getInstance().arenas.get(region).data.levelRequire);
                    } else {
                        return "NULL ARENA";
                    }
                }
            } else if (params.equalsIgnoreCase("player_top")) {
                return String.valueOf(dgPlayer.top2);
            }
        }
        return super.onRequest(player, params);
    }
}
