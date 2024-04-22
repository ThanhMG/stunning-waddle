package org.thanhmagics.dgcore.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.thanhmagics.dgcore.Arena;
import org.thanhmagics.dgcore.DGPlayer;
import org.thanhmagics.dgcore.PlayerUpgrade;
import org.thanhmagics.dgcore.arena.DGLocation;
import org.thanhmagics.dgcore.gui.SelectionGUI;
import org.thanhmagics.dgcore.gui.SetupGUI;
import org.thanhmagics.DGCore;
import org.thanhmagics.utils.XMaterial;

import java.util.HashMap;
import java.util.Map;

public class Listeners implements Listener {

    @EventHandler
    public void e1(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        DGPlayer dgPlayer = DGCore.getInstance().dataSerialize.player.get(player.getUniqueId().toString());
        SelectionGUI.guis.remove(dgPlayer.inv);
        SetupGUI.guis.remove(dgPlayer.inv);
        dgPlayer.inv = null;
    }

    @EventHandler
    public void e2(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!DGCore.getInstance().dataSerialize.player.containsKey(player.getUniqueId().toString())) {
            DGPlayer newplayer = new DGPlayer(player.getUniqueId());
            for (Arena arena : DGCore.getInstance().arenas.values()) {
//                for (String s : arena.data.regenCD.keySet())
//                    map.put(s,0);
                newplayer.mined_in_region.put(arena.id,0);
            }
            DGCore.getInstance().dataSerialize.player.put(newplayer.uuid.toString(), newplayer);
            newplayer.reloadMultiplier();
        } else {
            DGCore.getInstance().dataSerialize.player.get(player.getUniqueId().toString()).reloadMultiplier();
        }
    }

    @EventHandler
    public void e3(PlayerQuitEvent event) {
        Player player = (Player) event.getPlayer();
        DGPlayer dgPlayer = DGCore.getInstance().dataSerialize.player.get(player.getUniqueId().toString());
        dgPlayer.inv = null;
        SelectPosition.editor.remove(player.getUniqueId());
    }

    @EventHandler
    public void e4(BlockBreakEvent event) {
        if (!inside.containsKey(event.getBlock())) return;
        Block block = event.getBlock();
        event.setDropItems(false);
        event.setExpToDrop(0);
        Arena.blocks.put(block, block.getType());
        Arena.miner.put(block, event.getPlayer());
    }

    @EventHandler
    public void e5(EntityChangeBlockEvent event) {
        if (event.getBlock().getType().equals(XMaterial.FARMLAND.parseMaterial()) &&
                event.getTo().equals(XMaterial.DIRT.parseMaterial())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void e6(BlockFadeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void e7(EntityDamageByEntityEvent e) {
        Entity attacker = e.getDamager();
        Entity damaged = e.getEntity();

        if(attacker instanceof Player && damaged instanceof Player)
        {
            DGPlayer dgPlayer = DGCore.getInstance().dataSerialize.player.get(((Player) attacker).getPlayer().getUniqueId().toString());
            DGPlayer dg = DGCore.getInstance().dataSerialize.player.get(((Player) damaged).getPlayer().getUniqueId().toString());

            if (dgPlayer.pvpOff != null || dg.pvpOff != null)
                e.setCancelled(true);
        }
    }

//    @EventHandler
//    public void e7(PlayerInteractEvent event) {
//        if (event.getClickedBlock() != null) {
//            Block b = event.getClickedBlock();
//            if (b.getBlockData() instanceof Ageable age) {
//                age.
//            }
//        }
//    }

    public static void thread() {
        new Thread(() -> {
           while (DGCore.getInstance().isEnabled()) {
               for (Player player : Bukkit.getOnlinePlayers()) {
                   for (ItemStack is : player.getInventory().getContents()) {
                       int slot = -1;
                       for (int i = 0; i < player.getInventory().getContents().length; i++) {
                           if (player.getInventory().getContents()[i] != null) {
                               if (player.getInventory().getContents()[i].equals(is)) slot = i;
                           }
                       }
                       if (slot != -1) {
                           player.getInventory().setItem(slot,PlayerUpgrade.loreReplacer(is));
                       }
                   }
               }
               try {
                   Thread.sleep(500);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }
        }).start();
    }
    public static Map<Block,Arena> inside = new HashMap<>();

    public static void init() {
        inside.clear();
        for (Arena arena : DGCore.getInstance().arenas.values()) {
            for (DGLocation dgLocation : arena.data.region.keySet()) {
                inside.put(Bukkit.getWorld(dgLocation.w).getBlockAt(dgLocation.getLocation()),arena);
            }
        }
    }

//    public static void asyncEvent() {
//        new Thread(() -> {
//            while (DGCore.getInstance().status) {
//                for (Arena arena : DGCore.getInstance().arenas.values()) {
//                    World world = Bukkit.getWorld(arena.data.pos1.w);
//                    if (world == null) continue;
//                    for (Player player : world.getPlayers()) {
//                        if (Utils.insideBox(new DGLocation(player.getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(),
//                                player.getLocation().getBlockZ()), arena.data.pos1, arena.data.pos2, 5)) {
////                            if (!player.getGameMode().equals(GameMode.ADVENTURE)) {
////                                syncChangeGameMode(player,GameMode.ADVENTURE);
////                            }
//                            if (!inside.containsKey(player))
//                                inside.put(player, arena);
//                        } else {
////                            if (player.getGameMode().equals(GameMode.ADVENTURE))
////                                syncChangeGameMode(player,GameMode.ADVENTURE);
//                            inside.remove(player);
//                        }
//                    }
//                }
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }).start();
//    }

    private static void syncChangeGameMode(Player player, GameMode newGamemode) {
        Bukkit.getScheduler().runTask(DGCore.getInstance(), () -> player.setGameMode(newGamemode));
    }

}
