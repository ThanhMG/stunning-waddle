package org.thanhmagics.dgcore.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.thanhmagics.dgcore.Arena;
import org.thanhmagics.dgcore.arena.DGLocation;
import org.thanhmagics.dgcore.gui.SetupGUI;
import org.thanhmagics.dgcore.gui.SetupGUI2;
import org.thanhmagics.utils.Utils;

import java.util.*;

public class SelectPosition implements Listener {

    public static Map<UUID, List<DGLocation>> editor = new HashMap<>();

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (editor.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            Location bloc = event.getBlock().getLocation();
            DGLocation location = DGLocation.valueOf(bloc.getWorld().getName(), bloc.getBlockX(), bloc.getBlockY(), bloc.getBlockZ());
            List<DGLocation> list = editor.get(player.getUniqueId());
            list.add(location);
            if (list.size() == 3) {
                Arena arena = new Arena(list.get(0).w, list.get(1), list.get(2));
                editor.remove(player.getUniqueId());
                player.sendMessage(Utils.applyColor("&aĐặt Pos 2 Tại: " + location.toString()));
                player.sendMessage(Utils.applyColor("&aTạo Thành Công Region: " + arena.id));
                new SetupGUI2().open(player, arena);
            } else {
                if (list.size() == 2) {
                    player.sendMessage(Utils.applyColor("&aĐặt Pos 1 Tại: " + location.toString()));
                }
            }
        }
    }
}
