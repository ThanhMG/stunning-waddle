package org.thanhmagics.dgcore.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.thanhmagics.DGCore;
import org.thanhmagics.dgcore.DGPlayer;
import org.thanhmagics.utils.ItemBuilder;
import org.thanhmagics.utils.ItemData;
import org.thanhmagics.utils.Utils;

public class PVPCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (strings.length == 1) {
            if (strings[0].equalsIgnoreCase("on")) {
                DGPlayer dg = DGCore.getInstance().dataSerialize.player.get(player.getUniqueId().toString());
                dg.pvpOff = null;
                for (String mess : DGCore.getInstance().getConfig().getStringList("message.pvp_on_sucess")) {
                    player.sendMessage(Utils.applyColor(PlaceholderAPI.setPlaceholders(player, mess)));
                }
            } else if (strings[0].equalsIgnoreCase("off")) {
                if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                    if (DGCore.getInstance().dataSerialize.pvp_itemstack != null) {
                        ItemStack itemStack = ItemData.stringToIs(DGCore.getInstance().dataSerialize.pvp_itemstack);
                        if (itemStack != null && itemEqual(itemStack, player.getInventory().getItemInMainHand())) {
                            if (itemStack.getAmount() <= player.getInventory().getItemInMainHand().getAmount()) {
                                player.getInventory().getItemInMainHand().setAmount(
                                        player.getInventory().getItemInMainHand().getAmount() - itemStack.getAmount()
                                );
                                for (String mess : DGCore.getInstance().getConfig().getStringList("message.pvp_off_success")) {
                                    player.sendMessage(Utils.applyColor(PlaceholderAPI.setPlaceholders(player, mess)));
                                }
                                DGPlayer dg = DGCore.getInstance().dataSerialize.player.get(player.getUniqueId().toString());
                                if (dg.pvpOff == null) {
                                    dg.pvpOff = System.currentTimeMillis() + (Utils.stringToMs(DGCore.getInstance().getConfig().getString("pvp_off")));
                                } else {
                                    dg.pvpOff += System.currentTimeMillis() + (Utils.stringToMs(DGCore.getInstance().getConfig().getString("pvp_off")));
                                }
                            } else {
                                for (String mess : DGCore.getInstance().getConfig().getStringList("message.pvp_itemNotEnough")) {
                                    player.sendMessage(Utils.applyColor(PlaceholderAPI.setPlaceholders(player, mess)));
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean itemEqual(ItemStack itemStack, ItemStack is) {
        ItemStack i1 = itemStack.clone();
        i1.setAmount(1);
        ItemStack i2 = is.clone();
        i2.setAmount(1);
        return ItemBuilder.simpleEqual(i1, i2);
    }
}
