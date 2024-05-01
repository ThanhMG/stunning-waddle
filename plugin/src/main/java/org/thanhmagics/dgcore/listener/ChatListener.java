package org.thanhmagics.dgcore.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.thanhmagics.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class ChatListener implements Listener {

    private static Map<Player, Runnable> editor = new HashMap<>();

    public static void add(Player player, Runnable runnable) {
        if (!editor.containsKey(player)) {
            player.closeInventory();
            player.sendMessage(Utils.applyColor("&6-----------------------"));
            player.sendMessage(Utils.applyColor("&aNhập Giá Trị Xuống Chat"));
            player.sendMessage(Utils.applyColor("&aNhập &6cancel&a Để Thoát!"));
            player.sendMessage(Utils.applyColor("&6-----------------------"));
            editor.put(player, runnable);
        }
    }

    @EventHandler
    public void onEvent(PlayerChatEvent event) {
        if (editor.containsKey(event.getPlayer())) {
            event.setCancelled(true);
            editor.get(event.getPlayer()).run(event);
            editor.remove(event.getPlayer());
        }
    }

    public static abstract class Runnable {
        public abstract void run(PlayerChatEvent event);
    }
}
