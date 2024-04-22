package org.thanhmagics.dgcore.commands.tab;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.thanhmagics.DGCore;
import org.thanhmagics.dgcore.Arena;
import org.thanhmagics.dgcore.DGPlayer;
import org.thanhmagics.dgcore.EnchantType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ADCMTab implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if ((commandSender instanceof ConsoleCommandSender)) return null;
        List<String> result = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("core")) {
            if (args.length == 1) {
                result.add("create");
                result.add("edit");
                result.add("enchant");
                result.add("delete");
                result.add("boost");
                result.add("regions");
                result.add("setpvpitem");
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("delete")) {
                    result.addAll(DGCore.getInstance().arenas.keySet());
                } else if (args[0].equalsIgnoreCase("enchant")) {
                    result.add(EnchantType.MULTIPLE.name());
                    result.add(EnchantType.DROP.name());
                } else if (args[0].equalsIgnoreCase("boost")) {
                    for (DGPlayer player : DGCore.getInstance().dataSerialize.player.values())
                        result.add(Bukkit.getOfflinePlayer(player.uuid).getName());
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("boost")) {
                    result.add("1.0");
                    result.add("1.2");
                    result.add("1.4");
                    result.add("1.6");
                    result.add("1.8");
                    result.add("2.0");
                } else if (args[0].equalsIgnoreCase("enchant")) {
                    for (DGPlayer player : DGCore.getInstance().dataSerialize.player.values())
                        result.add(Bukkit.getOfflinePlayer(player.uuid).getName());
                }
            } else if (args.length == 4) {
                if (args[0].equalsIgnoreCase("enchant")) {
                    if (args[1].equalsIgnoreCase("DROP")) {
                        result.add("1");
                        result.add("2");
                        result.add("3");
                    } else if (args[1].equalsIgnoreCase("MULTIPLE")) {
                        for (Material m : Material.values())
                            result.add(m.name());
                    }
                    for (Arena arena : DGCore.getInstance().arenas.values())
                        result.add(arena.id);
                } else if (args[0].equalsIgnoreCase("boost")) {
                    result.add("0h1m");
                    result.add("1h30m");
                }
            } else if (args.length == 5) {
                if (args[0].equalsIgnoreCase("enchant")) {
                    if (args[1].equalsIgnoreCase("DROP")) {
                        result.addAll(DGCore.getInstance().arenas.keySet());
                    } else if (args[1].equalsIgnoreCase("MULTIPLE")) {
                        result.add("1");
                        result.add("2");
                        result.add("3");
                        result.add("4");
                        result.add("5");
                        result.add("6");
                    }
                }
            }
        }
        return result;
    }
}
