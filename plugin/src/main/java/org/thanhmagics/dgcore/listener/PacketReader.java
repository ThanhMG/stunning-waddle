package org.thanhmagics.dgcore.listener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.potion.PotionEffectType;
import org.thanhmagics.PPIBlockDig;
import org.thanhmagics.dgcore.DGPlayer;
import org.thanhmagics.dgcore.EnumUpgradeType;
import org.thanhmagics.dgcore.PlayerUpgrade;
import org.thanhmagics.DGCore;
import org.thanhmagics.dgcore.Arena;
import org.thanhmagics.utils.Utils;
import org.thanhmagics.utils.XMaterial;

import java.util.*;

public class PacketReader implements Listener {

    //    List<UUID> added = new ArrayList<>();
//
    public static List<XMaterial> farm1 = new ArrayList<>(Arrays.asList(XMaterial.POTATOES, XMaterial.CARROTS, XMaterial.MELON, XMaterial.BEETROOTS));
    public static List<XMaterial> farm2 = new ArrayList<>(Arrays.asList(XMaterial.POTATO, XMaterial.CARROT, XMaterial.MELON_SLICE, XMaterial.BEETROOT));
}
//
//
//    @EventHandler
//    public void onJoin(PlayerJoinEvent event) {
//        addPlayer(event.getPlayer());
//    }
//
//    @EventHandler
//    public void onQuit(PlayerQuitEvent event) {
//        added.remove(event.getPlayer().getUniqueId());
//    }
//
//    Map<Player, Progress> progress = new HashMap<>();
//
//    public class Progress {
//        public int i;
//
//        int x,y,z;
//        public long start;
//
//        public Double phase = null;
//
//        public Progress(int i, int x,int y, int z, long start) {
//            this.i = i;
//            this.x = x;
//            this.y = y;
//            this.z = z;
//            this.start = start;
//        }
//
//        public Location bukkitLoc(World w) {
//            return new Location(w, x,y,z);
//        }
//    }
//
//    public void thread() {
//        new Thread(() -> {
//            while (DGCore.getInstance().isEnabled()) {
//                    for (Player key : progress.keySet()) {
//                        Progress value = progress.get(key);
//                        Arena arena = Listeners.inside.get(key.getWorld().getBlockAt(value.bukkitLoc(key.getWorld())));
//                        if (arena == null) continue;
//                        DGPlayer dgPlayer = DGCore.getInstance().dataSerialize.player.get(key.getUniqueId().toString());
////                    Bukkit.getScheduler().runTask(DGCore.getInstance(),
////                            () -> {
//                        double phase = 0;
//                        Block block = key.getWorld().getBlockAt(value.bukkitLoc(key.getWorld()).getBlockX(),
//                                value.bukkitLoc(key.getWorld()).getBlockY(), value.bukkitLoc(key.getWorld()).getBlockZ());
//                        if (arena.data.miningSpeed.containsKey(block.getType().name()))
//                            phase = arena.data.miningSpeed.get(block.getType().name());
//                        double t = 0;
//                        if (phase != 0) {
//                            if (value.phase == null) {
//                                if (dgPlayer.playerUpgrades.containsKey(EnumUpgradeType.HASTE)) {
//                                    int lvl = dgPlayer.playerUpgrades.get(EnumUpgradeType.HASTE);
//                                    try {
//                                        phase = (int) (phase / Double.parseDouble(PlayerUpgrade.upgrades.get(EnumUpgradeType.HASTE).getValue(lvl) + 1));
//                                    } catch (Exception e) {
//                                        System.out.println("<!> ERROR: player upgrade level lớn hơn số level cho phép!");
//                                    }
//                                    ItemStack itemStack = key.getInventory().getItemInMainHand();
//                                    if (itemStack.getType() != Material.AIR && itemStack.getEnchantments().containsKey(Enchantment.DIG_SPEED)) {
//                                        int level = itemStack.getEnchantments().get(Enchantment.DIG_SPEED);
//                                        int d = DGCore.getInstance().efficiency_file.config.getInt("haste.level." + level + ".buff");
//                                        phase = (phase / 100) * (100 - d);
//                                    }
//                                    value.phase = phase;
//                                }
//                            } else {
//                                phase = value.phase;
//                            }
//                            t = value.start + phase;
//                            //phase = phase / 9;
//                            int i = 9 - ((int) ((t - System.currentTimeMillis()) / (phase / 9)));
//                            //   key.sendMessage(String.valueOf((t - System.currentTimeMillis()) + " | " + phase));
////                                    DGCore.getInstance().nmsManager.sendPacket(key,
////                                            new PacketPlayOutBlockBreakAnimation(0, value.block, i));
//                            DGCore.getInstance().nmsManager.sendBlockBreakAnimation(key,
//                                    value.x, value.y, value.z, i);
//                        }
//                        if (System.currentTimeMillis() > t) {
//                            Arena.blocks.put(block, block.getType());
//                            Arena.miner.put(block, key);
//                            progress.remove(key);
//                            removeEffect(key);
//                            DGCore.getInstance().nmsManager.sendBlockBreakAnimation(key, block.getX(), block.getY(), block.getZ(), -1);
//                            Bukkit.getScheduler().runTask(DGCore.getInstance(), () -> block.setType(Material.AIR));
//                        }
//                        //  });
//                    }
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }).start();
//    }
//
//    public void addPlayer(Player player) {
//        if (added.contains(player.getUniqueId()))
//            return;
//        added.add(player.getUniqueId());
//        ChannelDuplexHandler duplexHandler = new ChannelDuplexHandler() {
//
//            @Override
//            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                if (msg.getClass().getName().contains("PacketPlayInBlockDig")) {
//                    PPIBlockDig ppiBlockDig = DGCore.getInstance().nmsManager.packet(msg);
//                    ppiBlockDig.init();
//                    if (ppiBlockDig.type.equals("ABORT_DESTROY_BLOCK") || ppiBlockDig.type.equalsIgnoreCase("STOP_DESTROY_BLOCK")) {
//                        progress.remove(player);
//                        removeEffect(player);
//                    }
//                    Progress pg = new Progress(0, ppiBlockDig.x, ppiBlockDig.y, ppiBlockDig.z, System.currentTimeMillis());
//                    Block wb = player.getWorld().getBlockAt(pg.bukkitLoc(player.getWorld()));
//                    if (Listeners.inside.containsKey(wb)) {
//                        if (player.getGameMode().equals(GameMode.SURVIVAL)) {
//                            Block block = player.getWorld().getBlockAt(ppiBlockDig.x, ppiBlockDig.y, ppiBlockDig.z);
//                            if (Listeners.inside.get(wb).data.region.containsValue(block.getType().name())) {
//                                if (ppiBlockDig.type.equalsIgnoreCase("START_DESTROY_BLOCK")) {
//                                    if (!PacketReader.farm1.contains(XMaterial.matchXMaterial(block.getType()))) {
//                                        if (progress.containsKey(player)) progress.replace(player, pg);
//                                        else progress.put(player, pg);
//                                        Bukkit.getScheduler().runTask(DGCore.getInstance(), () -> player.addPotionEffect(PotionEffectType.SLOW_DIGGING.createEffect(Integer.MAX_VALUE, 255)));
//                                    } else {
//                                        Arena.blocks.put(block, block.getType());
//                                        Arena.miner.put(block, player);
//                                        Bukkit.getScheduler().runTask(DGCore.getInstance(), () -> block.setType(Material.AIR));
//                                    }
//                                } else if (ppiBlockDig.type.equalsIgnoreCase("ABORT_DESTROY_BLOCK")) {
//                                    progress.remove(player);
//                                    removeEffect(player);
//                                    DGCore.getInstance().nmsManager.sendBlockBreakAnimation(player, ppiBlockDig.x, ppiBlockDig.y, ppiBlockDig.z, 10);
//                                }
//                            }
//                        } else if (player.getGameMode().equals(GameMode.CREATIVE)) {
//                            player.sendMessage(Utils.applyColor("&c<!> Hệ Thống Không Thể Hoạt Động Đối Vơi GameMode1!"));
//                        }
//                    }
//                }
//                super.channelRead(ctx, msg);
//            }
//        };
//        try {
//            DGCore.getInstance().nmsManager.getChannel(player)
//                    .pipeline().addBefore("packet_handler", String.valueOf(System.currentTimeMillis()), duplexHandler);
//        } catch (IllegalPluginAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void removeEffect(Player player) {
//        Bukkit.getScheduler().runTask(DGCore.getInstance(), new Runnable() {
//            @Override
//            public void run() {
//                player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
//            }
//        });
//    }
//}
