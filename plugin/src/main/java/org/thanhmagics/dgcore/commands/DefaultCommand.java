package org.thanhmagics.dgcore.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.thanhmagics.DGCore;
import org.thanhmagics.dgcore.gui.PlayerUpgradeGUI;
import org.thanhmagics.utils.Utils;

import java.util.Objects;

public class DefaultCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof ConsoleCommandSender) return true;
        Player player = (Player) commandSender;
        if (player.hasPermission(Objects.requireNonNull(DGCore.getInstance().upgrade_gui.config.getString("permission")))) {
            new PlayerUpgradeGUI().open(player);
        } else {
            for (String str : DGCore.getInstance().getConfig().getStringList("message.nonPermission"))
                player.sendMessage(Utils.applyColor(PlaceholderAPI.setPlaceholders(player,str)));
        }
        return true;
    }
}
