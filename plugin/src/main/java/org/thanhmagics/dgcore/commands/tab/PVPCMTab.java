package org.thanhmagics.dgcore.commands.tab;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class PVPCMTab implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> rs = new ArrayList<>();
        if (strings.length == 1) {
            rs.add("on");
            rs.add("off");
        }
        return rs;
    }
}
