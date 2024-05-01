package org.thanhmagics.dgcore;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;
import org.thanhmagics.DGCore;
import org.thanhmagics.utils.Utils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class Multiplier implements Serializable {


    public abstract double mul();

    public abstract long end();

    public static void addMulti(DGPlayer player, long end, double multi) {
        player.mul.add(new Multiplier() {
            @Override
            public double mul() {
                return multi;
            }

            @Override
            public long end() {
                return end;
            }
        });
        player.reloadMultiplier();
    }

    static Map<String, Integer> amount = new HashMap<>();
    static Map<String, String> display = new HashMap<>();
    static Map<DGPlayer, List<String>> perms = new HashMap<>();

    public static void asyncChecker() {
        for (String k1 : DGCore.getInstance().getConfig().getConfigurationSection("perm").getKeys(false)) {
            amount.put(k1, DGCore.getInstance().getConfig().getInt("perm." + k1 + ".amount"));
            display.put(k1, DGCore.getInstance().getConfig().getString("perm." + k1 + ".display"));
        }
        new Thread(() -> {
            while (DGCore.getInstance().isEnabled()) {
                try {
                    for (DGPlayer dgPlayer : DGCore.getInstance().dataSerialize.player.values()) {
                        if (dgPlayer.l < System.currentTimeMillis()) {
                            dgPlayer.lm = null;
                            dgPlayer.lma = -1;
                        }
                        Iterator<Multiplier> iterator = dgPlayer.mul.iterator();
                        while (iterator.hasNext()) {
                            Multiplier m = iterator.next();
                            if (System.currentTimeMillis() > m.end()) {
                                iterator.remove();
                                dgPlayer.reloadMultiplier();
                            }
                        }
                    }
                    perms.clear();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        List<PermissionAttachmentInfo> strings = new ArrayList<>(player.getEffectivePermissions());
                        DGPlayer dgPlayer = DGCore.getInstance().dataSerialize.player.get(player.getUniqueId().toString());
                        perms.put(dgPlayer, new ArrayList<>());
                        strings.forEach(permissionAttachmentInfo -> {
                            perms.get(dgPlayer).add(permissionAttachmentInfo.getPermission());
                        });
                    }
                    for (DGPlayer dgPlayer : perms.keySet()) {
                        if (dgPlayer == null) continue;
                        List<String> p = perms.get(dgPlayer);
                        if (p == null) continue;
                        for (String perm : p) {
                            if (amount.containsKey(perm)) {
                                int mul = amount.get(perm);
                                String d = display.get(perm);
                                dgPlayer.f_mul = mul;
                                dgPlayer.display = d;
                                break;
                            } else {
                                dgPlayer.display = null;
                                dgPlayer.f_mul = 0;
                            }
                        }
                    }
                    for (DGPlayer dgPlayer : DGCore.getInstance().dataSerialize.player.values()) {
                        if (dgPlayer.pvpOff != null) {
                            if (dgPlayer.pvpOff < System.currentTimeMillis()) {
                                Bukkit.getScheduler().runTask(DGCore.getInstance(), () -> {
                                    for (String s : DGCore.getInstance().getConfig().getStringList("message.pvp_off_overtime"))
                                        Bukkit.getPlayer(dgPlayer.uuid).sendMessage(Utils.applyColor(PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(dgPlayer.uuid), s)));
                                });
                                dgPlayer.pvpOff = null;
                            }
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } catch (ConcurrentModificationException ignored) {
                }
            }
        }).start();
    }

}
