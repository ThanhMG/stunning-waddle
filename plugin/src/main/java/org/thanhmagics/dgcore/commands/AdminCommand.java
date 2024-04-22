package org.thanhmagics.dgcore.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.thanhmagics.dgcore.*;
import org.thanhmagics.dgcore.arena.DGLocation;
import org.thanhmagics.dgcore.gui.SetupGUI;
import org.thanhmagics.dgcore.gui.SetupGUI2;
import org.thanhmagics.dgcore.listener.Listeners;
import org.thanhmagics.dgcore.listener.SelectPosition;
import org.thanhmagics.DGCore;
import org.thanhmagics.utils.ItemBuilder;
import org.thanhmagics.utils.ItemData;
import org.thanhmagics.utils.Utils;
import org.thanhmagics.utils.XMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class AdminCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
       // if (commandSender instanceof ConsoleCommandSender) return true;
        if (!commandSender.hasPermission("op") || !commandSender.hasPermission("core.admin")) {
            for (String str : DGCore.getInstance().getConfig().getStringList("command.nonPermission")) {
                commandSender.sendMessage(Utils.applyColor(str));
            }
            return true;
        }
        if (args.length == 0) {
            for (String str : DGCore.getInstance().getConfig().getStringList("command.admin")) {
                commandSender.sendMessage(Utils.applyColor(str));
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("regions")) {
                commandSender.sendMessage(Utils.applyColor("&d&m<----->&a&lRegions&d&m<----->"));
                for (String region : DGCore.getInstance().dataSerialize.arena.keySet()) {
                    commandSender.sendMessage(Utils.applyColor("&7- &6" + region));
                }
                commandSender.sendMessage(Utils.applyColor("&d&m<--------------->"));
                return true;
            } else if (args[0].equalsIgnoreCase("setpvpitem")) {
                Player player = (Player) commandSender;
                if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    DGCore.getInstance().dataSerialize.pvp_itemstack = ItemData.isToString(itemStack);
                    player.sendMessage(Utils.applyColor("&aĐặt Thành Công:&f " + ItemBuilder.getDisplayName(itemStack)));
                } else {
                    player.sendMessage(Utils.applyColor("&cCầm Vật Phẩm Trên Tay!"));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                FileConfig.reload();
                PlayerUpgrade.upgrades.clear();
                new PlayerUpgrade("multiplier",DGCore.getInstance().upgrade_file.config, EnumUpgradeType.MULTIPLIER).init();
                new PlayerUpgrade("ci_drop",DGCore.getInstance().upgrade_file.config, EnumUpgradeType.CI_DROP).init();
                new PlayerUpgrade("token",DGCore.getInstance().upgrade_file.config, EnumUpgradeType.TOKEN).init();
                commandSender.sendMessage(Utils.applyColor("&aReload Thành Công!"));
            }
            for (String str : DGCore.getInstance().getConfig().getStringList("command.admin")) {
                commandSender.sendMessage(Utils.applyColor(str));
            }
        } else if (args.length == 2) {
            String string = args[0];
            if (string.equalsIgnoreCase("edit")) {
                if (commandSender instanceof ConsoleCommandSender) return true;
                Player player = (Player) commandSender;
                String name = args[1].toLowerCase();
                if (!DGCore.getInstance().dataSerialize.arena.containsKey(name)) {
                    commandSender.sendMessage(Utils.applyColor("&cRegion Không Tồn Tại!"));
                    return true;
                }
                new SetupGUI2().open(player, DGCore.getInstance().arenas.get(name));
            } else if (string.equalsIgnoreCase("create")) {
                if (commandSender instanceof ConsoleCommandSender) return true;
                Player player = (Player) commandSender;
                String name = args[1].toLowerCase();
                if (SelectPosition.editor.containsKey(player.getUniqueId())) {
                    commandSender.sendMessage(Utils.applyColor("&cVui Lòng Hoàn Thành Tạo Region Trước Đó"));
                    return true;
                }
                if (name.length() == 0) {
                    commandSender.sendMessage(Utils.applyColor("&Tên Quá Ngắn!"));
                    return true;
                }
                if (!DGCore.getInstance().dataSerialize.arena.containsKey(name)) {
                    if (name.contains(":")) {
                        commandSender.sendMessage(Utils.applyColor("&cTên Chứa Kí Tự Không Hợp Lệ"));
                        return true;
                    }
                    SelectPosition.editor.put(player.getUniqueId(), new ArrayList<>(Arrays.asList(new DGLocation(name,0,0,0))));
                    commandSender.sendMessage(Utils.applyColor("&aVui Lòng Phá Hủy 2 Block Tượng Trưng Cho 2 Pos Của Region"));
                } else {
                    commandSender.sendMessage(Utils.applyColor("&cTên Đã Tồn Tại!"));
                }
            } else if (string.equalsIgnoreCase("delete")) {
                String name = args[1].toLowerCase();
                if (DGCore.getInstance().dataSerialize.arena.containsKey(name)) {
                    DGCore.getInstance().arenas.get(name).status = false;
                    DGCore.getInstance().dataSerialize.arena.remove(name);
                    DGCore.getInstance().arenas.remove(name);
                    Listeners.init();
                    commandSender.sendMessage(Utils.applyColor("&aXoa Thanh Cong!"));
                } else {
                    commandSender.sendMessage(Utils.applyColor("&cTên Không Tồn Tại!"));
                }
            } else {
                for (String str : DGCore.getInstance().getConfig().getStringList("command.admin")) {
                    commandSender.sendMessage(Utils.applyColor(str));
                }
            }
        } else if (args.length == 5) {
            if (args[0].equalsIgnoreCase("enchant") || args[0].equalsIgnoreCase("enc")) {
                OfflinePlayer p = Bukkit.getOfflinePlayer(args[2]);
                if (!p.isOnline()) {
                    commandSender.sendMessage(Utils.applyColor("&aPlayer Khong Online!"));
                    return true;
                }
                Player player = p.getPlayer();
                if (player == null) {
                    commandSender.sendMessage("Player Khong Online!");
                    return true;
                }
                DGPlayer dgPlayer = DGCore.getInstance().dataSerialize.player.get(p.getUniqueId().toString());
                if (dgPlayer == null) {
                    commandSender.sendMessage(Utils.applyColor("&cPlayer Khong Ton Tai!"));
                    return true;
                } else {
                    if (args[1].equalsIgnoreCase("MULTIPLE") || args[1].equalsIgnoreCase("DROP")) {
                        EnchantType enchantType = EnchantType.valueOf(args[1].toUpperCase());
                        if (enchantType.equals(EnchantType.MULTIPLE)) {
                            Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(args[4].toUpperCase());
                            if (xMaterial.isPresent()) {
                                XMaterial material = xMaterial.get();
                                double value = Double.parseDouble(args[3]);
                                addNBT(commandSender,material.parseMaterial().name(),player,enchantType,String.valueOf(value));
                            } else {
                                commandSender.sendMessage(Utils.applyColor("&cMaterial K Tồn Tại!"));
                            }
                        } else if (enchantType.equals(EnchantType.DROP)) {
                            double value = Double.parseDouble(args[3]);
                            if (DGCore.getInstance().arenas.containsKey(args[4].toLowerCase())) {
                                addNBT(commandSender,args[4].toLowerCase(),player,enchantType, String.valueOf(value));
                            } else {
                                commandSender.sendMessage(Utils.applyColor("&cRegion Không Tồn Tại!"));
                            }
                        }
                    } else {
                        commandSender.sendMessage(Utils.applyColor("&cEnchant Không Tồn Tại!"));
                    }
                }
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("boost")) {
                OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
                DGPlayer dgPlayer = DGCore.getInstance().dataSerialize.player.get(p.getUniqueId().toString());
                if (dgPlayer == null) {
                    commandSender.sendMessage(Utils.applyColor("&cPlayer Khong Ton Tai!"));
                    return true;
                } else {
                    try {
                        double d = Double.parseDouble(args[2]);
                        String time = args[3];
                        Multiplier.addMulti(dgPlayer,Long.sum(Utils.stringToMs(time), System.currentTimeMillis()),d);
                        if (p.isOnline()) {
                            for (String string : DGCore.getInstance().getConfig().getStringList("message.booster")) {
                                p.getPlayer().sendMessage(Utils.applyColor(
                                        PlaceholderAPI.setPlaceholders(p.getPlayer(),string.replace("{multi}",String.valueOf(d)))
                                                .replace("{time}",time)
                                ));
                            }
                        }
                    } catch (Exception e) {
                        commandSender.sendMessage(Utils.applyColor("&cLoi Cu Phap Command!"));
                        e.printStackTrace();
                    }
                }
            } else if (args[0].equalsIgnoreCase("enchant")) {
                if (args[1].equalsIgnoreCase("drop")) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(args[2]);
                    if (!p.isOnline()) {
                        commandSender.sendMessage(Utils.applyColor("&aPlayer Khong Online!"));
                        return true;
                    }
                    Player player = p.getPlayer();
                    if (player == null) {
                        commandSender.sendMessage("Player Khong Online!");
                        return true;
                    }
                    double value = Double.parseDouble(args[3]);
                    addNBT(commandSender, "", player, EnchantType.DROP, String.valueOf(value));
                } else if (args[1].equalsIgnoreCase("multiple")) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(args[2]);
                    if (!p.isOnline()) {
                        commandSender.sendMessage(Utils.applyColor("&aPlayer Khong Online!"));
                        return true;
                    }
                    Player player = p.getPlayer();
                    if (player == null) {
                        commandSender.sendMessage("Player Khong Online!");
                        return true;
                    }
                    double value = Double.parseDouble(args[3]);
                    addNBT(commandSender, "", player, EnchantType.MULTIPLE, String.valueOf(value));
                } else {
                    commandSender.sendMessage(Utils.applyColor("&c? enchant"));
                }
            }
        }
        return true;
    }

    private void addNBT(CommandSender commandSender, String s1, Player player, EnchantType ect, String s2) {
        if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            player.getInventory().setItem(
                    player.getInventory().getHeldItemSlot(),
                    DGCore.getInstance().nmsManager.nbtStorage().put(player.getInventory().getItemInMainHand(), "dgcore:" + ect.name() + ":" + s1, s2));
        } else {
            commandSender.sendMessage(Utils.applyColor("&cPlayer Khong Co Item Tren Main Hand"));
        }
    }
}
