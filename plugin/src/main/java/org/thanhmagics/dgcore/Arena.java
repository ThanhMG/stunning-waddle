package org.thanhmagics.dgcore;

import me.clip.placeholderapi.PlaceholderAPI;
import me.realized.tm.api.TMAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.thanhmagics.dgcore.arena.ArenaData;
import org.thanhmagics.dgcore.arena.CIDropChance;
import org.thanhmagics.dgcore.arena.DGLocation;
import org.thanhmagics.dgcore.listener.PacketReader;
import org.thanhmagics.DGCore;
import org.thanhmagics.dgcore.gui.SelectionGUI;
import org.thanhmagics.dgcore.listener.Listeners;
import org.thanhmagics.utils.ItemData;
import org.thanhmagics.utils.Utils;
import org.thanhmagics.utils.XMaterial;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Arena {

    public String id;

    public ArenaData data;

    public long regenRegion;

    public boolean status = true;

    public static Map<DGPlayer,BossBar> bossBar = new ConcurrentHashMap<>();

    public List<Player> players = new ArrayList<>();

    public Map<Integer,DGPlayer> topPlayer = new ConcurrentHashMap<>();
    public static Map<Integer,DGPlayer> top_Player = new ConcurrentHashMap<>();

    private Map<Long, Block> broken = new ConcurrentHashMap<>();

    public static Map<Block, Material> blocks = new ConcurrentHashMap<>();

    public static Map<Block, Player> miner = new ConcurrentHashMap<>();

    public Arena(String id, DGLocation pos1, DGLocation pos2) {
        this.id = id.toLowerCase();
        data = new ArenaData();
        data.id = id;
        data.pos1 = pos1;
        data.pos2 = pos2;
        regenRegion = System.currentTimeMillis();
        data.permission = "core.mined." + id;
        World w = Bukkit.getWorld(pos1.w);
        if (w == null)
            throw new RuntimeException("world " + pos1.w + " == null");
        List<String> materials = new ArrayList<>();
        for (int i = Math.min(pos1.x, pos2.x); i <= Math.max(pos1.x, pos2.x); i++) {
            for (int j = Math.min(pos1.y, pos2.y); j <= Math.max(pos1.y, pos2.y); j++) {
                for (int k = Math.min(pos1.z, pos2.z); k <= Math.max(pos1.z, pos2.z); k++) {
                    Block block = w.getBlockAt(i, j, k);
                    if (block.getType().equals(Material.AIR)) continue;
                    data.region.put(DGLocation.valueOf(pos1.w, i, j, k), (block.getType().name()));
                    if (materials.contains(block.getType().name())) continue;
                    materials.add(block.getType().name());
                    data.expDrop.put(block.getType().name(),1);
                  //  data.regenCD.put(block.getType().name(), 5000);
                    data.regenCD_per_block.put(block.getType().name(),5000);
                    if (PacketReader.farm1.contains(XMaterial.matchXMaterial(block.getType()))) continue;
                    data.miningSpeed.put(block.getType().name(), 5000.0);
                }
            }
        }
//        Map<String,Integer> map = new HashMap<>();
//        for (String s : data.regenCD_per_block.keySet())
//            map.put(s,0);
        for (DGPlayer player : DGCore.getInstance().dataSerialize.player.values()) {
          //  player.mined_in_region.put(id,map);
            player.mined_in_region.put(id,0);
        }
//        data.encRequire.put(EnumType.BLOCK,0);
//        data.encRequire.put(EnumType.CROP,0);
//        data.enc = EnumType.BLOCK;
        List<String> strings = new ArrayList<>();
        for (String region : data.region.values()) {
            if (strings.contains(region)) continue;
            strings.add(region);
            data.ciBlockDropChance.put(region,new ArrayList<>());
        }
        data.encRequire.put(EnchantType.DROP,0);
        data.encRequire.put(EnchantType.MULTIPLE,0);
        DGCore.getInstance().dataSerialize.arena.put(id.toLowerCase(), data);
        DGCore.getInstance().arenas.put(id.toLowerCase(), this);
        asyncBlockRegen();
        Listeners.init();
    }

    public Arena(ArenaData data) {
        this.data = data;
        this.id = data.id;
        regenRegion = System.currentTimeMillis();
        DGCore.getInstance().arenas.put(id.toLowerCase(), this);
        asyncBlockRegen();
        Listeners.init();
    }

    public void asyncBlockRegen() {
        new Thread(() -> {
            while (DGCore.getInstance().isEnabled()) {
                    Iterator<Map.Entry<Long, Block>> iterator = broken.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Long, Block> entry = iterator.next();
                        Long l = entry.getKey();
                        Block block = entry.getValue();
                        if (System.currentTimeMillis() > l) {
                            iterator.remove();
                            Bukkit.getScheduler().runTask(DGCore.getInstance(), () -> {
                                Material rs = getMaterialForBlock(block);
                                block.setType(rs == null ? Material.STONE : rs);
                                block_age(block);
                            });
                        }
                    }
                    if (data.regenMode.equals(RegenMode.TIME)) {
                        regenAll();
                    } else if (data.regenMode.equals(RegenMode.EMPTY)) {
                        regenIfEmpty();
                    }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private Material getMaterialForBlock(Block block) {
        Material rs = null;
        DGLocation loc = DGLocation.valueOf(block.getWorld().getName(),
                block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
        for (DGLocation gl : data.region.keySet()) {
            if (gl.equals(loc))
                rs = XMaterial.matchXMaterial(data.region.get(gl)).get().parseMaterial();
        }
        return rs;
    }

    private void block_age(Block block) {
        if (block.getBlockData() instanceof Ageable ageable) {
            ageable.setAge(ageable.getMaximumAge());
            block.setBlockData(ageable);
        }
    }

    private void regenAll() {
        if (System.currentTimeMillis() > regenRegion) {
            regenRegion = System.currentTimeMillis() + data.regenCD_time;
            broken.clear();
            for (Map.Entry<DGLocation, String> entry : data.region.entrySet()) {
                DGLocation dgl = entry.getKey();
                Material value = XMaterial.valueOf(entry.getValue()).parseMaterial();
                if (value == null) continue;
                try {
                    Bukkit.getScheduler().runTask(DGCore.getInstance(), () -> {
                        Block b = Objects.requireNonNull(Bukkit.getWorld(dgl.w)).getBlockAt(dgl.x, dgl.y, dgl.z);
                        if (!b.getType().equals(value)) b.setType(value);
                        brokenn.remove(b);
                        block_age(b);
                    });
                } catch (Exception ignored) {}
            }
        }
    }

    public static void asyncBlockTrack() {
        new Thread(() -> {
            while (DGCore.getInstance().isEnabled()) {
                    Iterator<Map.Entry<Block, Material>> iterator = new HashMap<>(blocks).entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Block, Material> entry = iterator.next();
                        Block block = entry.getKey();
                        Material material = entry.getValue();
                        Player miner = Arena.miner.get(block);
                        if (miner == null) continue;
                        DGLocation location = DGLocation.valueOf(block.getWorld().getName(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
                        for (Arena arena : new ArrayList<>(DGCore.getInstance().arenas.values())) {
                            if (Utils.insideBox(location, arena.data.pos1, arena.data.pos2, 0)) {
                                reward(arena, block, material, miner);
                                iterator.remove();
                                Arena.miner.remove(block);
                            }
                        }
                    }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void regenIfEmpty() {
        for (DGLocation dgLocation : data.region.keySet()) {
            if (!Bukkit.getWorld(dgLocation.w).getBlockAt(dgLocation.getLocation()).getType().equals(Material.AIR)) return;
        }
        try {
            Bukkit.getScheduler().runTask(DGCore.getInstance(), () -> {
                for (DGLocation location : data.region.keySet()) {
                    Block block = Bukkit.getWorld(location.w).getBlockAt(location.getLocation());
                    block.setType(
                            Objects.requireNonNull(XMaterial.matchXMaterial(data.region.get(location)).get().parseMaterial()));
                    brokenn.remove(block);
                    block_age(block);
                }
            });
        } catch (Exception ignored) {}
    }

    public List<Block> brokenn = new ArrayList<>();
    
    public void replaceBlock(Block block,Material material) {
        Bukkit.getScheduler().runTask(DGCore.getInstance(), () -> {block.setType(material); block_age(block);});
    }

    private static void reward(Arena arena, Block block, Material material, Player miner) {
        if (!miner.isOnline()) return;
        if (arena.brokenn.contains(block)) {
            arena.replaceBlock(block,material);
            return;
        }
        DGPlayer dgPlayer = DGCore.getInstance().dataSerialize.player.get(miner.getUniqueId().toString());
        Material rs = arena.getMaterialForBlock(block);
        if (rs == null) {
            for (DGLocation e : arena.data.region.keySet()) {
                if (e.equals(DGLocation.valueOf(block.getWorld().getName(),block.getLocation().getBlockX(),
                        block.getLocation().getBlockY(),block.getLocation().getBlockZ())))
                    continue;
                Bukkit.getScheduler().runTask(DGCore.getInstance(), () -> {block.setType(Material.STONE);});
                break;
            }
            return;
        }
        if (!miner.hasPermission(arena.data.permission)) {
            for (String s : DGCore.getInstance().getConfig().getStringList("message.nonPermission"))
                miner.sendMessage(Utils.applyColor(PlaceholderAPI.setPlaceholders(miner,s)));
            arena.replaceBlock(block,rs);
            return;
        }

        if (dgPlayer.level < arena.data.levelRequire) {
            for (String s : DGCore.getInstance().getConfig().getStringList("message.nonLevel"))
                miner.sendMessage(Utils.applyColor(PlaceholderAPI.setPlaceholders(miner,s)));
            arena.replaceBlock(block,rs);
            return;
        }
        double deviation = 1;
//        int lvlRequire = arena.data.encRequire.get(arena.data.enc);
//        String toolLevel = null;
//        if (!miner.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
//            toolLevel = (DGCore.getInstance().nmsManager.nbtStorage().get(miner.getInventory().getItemInMainHand(), "dgcore:" + EnchantType.MULTIPLE.name() + ":" + material.name()));
//        }
//        if (toolLevel == null || toolLevel == "")
//            toolLevel = "0";
//        if (lvlRequire != 0) {
//            if (toolLevel.length() == 0 || Integer.parseInt(toolLevel) < lvlRequire) {
//                for (String s : DGCore.getInstance().getConfig().getStringList("message.encNotEnough")) {
//                    miner.sendMessage(Utils.applyColor(s.replace("{level_require}", String.valueOf(lvlRequire)).replace("{level}",
//                            toolLevel)));
//                }
//                arena.replaceBlock(block, rs);
//                return;
//            }
//            if (miner.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
//                for (String s : DGCore.getInstance().getConfig().getStringList("message.toolError"))
//                    miner.sendMessage(Utils.applyColor(PlaceholderAPI.setPlaceholders(miner, s)));
//                arena.replaceBlock(block, rs);
//                return;
//            }
//        }
//        long cd = System.currentTimeMillis() + arena.data.regenCD.get(material.name());
//        arena.broken.put(cd, block);
        if (arena.data.regenMode.equals(RegenMode.PER_BLOCK)) {
            if (arena.data.regenCD_per_block.containsKey(material.name())) {
                arena.broken.put(Long.sum(System.currentTimeMillis(), arena.data.regenCD_per_block.get(material.name())), block);
            } else {
                arena.replaceBlock(block,material);
                return;
            }
        }
        if (dgPlayer.playerUpgrades.containsKey(EnumUpgradeType.CI_DROP)) {
            int lvl = dgPlayer.playerUpgrades.get(EnumUpgradeType.CI_DROP);
            deviation += Double.parseDouble(PlayerUpgrade.upgrades.get(EnumUpgradeType.CI_DROP).getValue(lvl));
        }
//        CIDropChance cidrop = arena.a(arena.data.ciDropChances,deviation);
//        if (cidrop != null && !Utils.isInvFull(miner.getInventory()))
//            miner.getInventory().addItem(ItemData.stringToIs(cidrop.data));
        String lvl1 = null;
        if (!miner.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            ItemStack itemStack = miner.getInventory().getItemInMainHand();
            lvl1 = DGCore.getInstance().nmsManager.nbtStorage().get(itemStack,"dgcore:" + EnchantType.DROP.name() + ":" + arena.id.toLowerCase());
            if (lvl1 == null || lvl1.length() == 0) {
                lvl1 = DGCore.getInstance().nmsManager.nbtStorage().get(itemStack,"dgcore:" + EnchantType.DROP.name() + ":");
            }
            if (lvl1 != null && lvl1.length() != 0) {
                deviation += Double.parseDouble(lvl1);
            }
        }
        if (arena.data.encRequire.get(EnchantType.DROP) != 0) {
            if (miner.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                for (String s : DGCore.getInstance().getConfig().getStringList("message.toolError"))
                    miner.sendMessage(Utils.applyColor(PlaceholderAPI.setPlaceholders(miner, s)));
                arena.replaceBlock(block,rs);
            }
            if (lvl1 == null || lvl1.length() == 0|| Double.parseDouble(lvl1) < arena.data.encRequire.get(EnchantType.DROP)) {
                for (String s : DGCore.getInstance().getConfig().getStringList("message.encNotEnough")) {
                    miner.sendMessage(Utils.applyColor(s.replace("{level_require}", String.valueOf(arena.data.encRequire.get(EnchantType.DROP))).replace("{level}",
                            lvl1 == null ? "0" : lvl1)));
                }
                arena.replaceBlock(block,rs);
                return;
            }
        }
        String mn = SelectionGUI.materialConverterAB(rs).name();
        double i1 = dgPlayer.multi;
        if (!miner.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            ItemStack itemStack = miner.getInventory().getItemInMainHand();
            String lvl = DGCore.getInstance().nmsManager.nbtStorage().get(itemStack, "dgcore:" + EnchantType.MULTIPLE.name() + ":" + rs.name());
            if (lvl == null || lvl.length() == 0) {
                lvl = DGCore.getInstance().nmsManager.nbtStorage().get(itemStack,"dgcore:" + EnchantType.MULTIPLE.name() + ":");
            }
            if (lvl != null && !lvl.isEmpty() && Double.parseDouble(lvl) >= arena.data.encRequire.get(EnchantType.MULTIPLE)) {
                i1 = dgPlayer.multi + Double.parseDouble(lvl);
            } else {
                if (arena.data.encRequire.get(EnchantType.MULTIPLE) != 0) {
                    for (String s : DGCore.getInstance().getConfig().getStringList("message.encNotEnough")) {
                        miner.sendMessage(Utils.applyColor(s.replace("{level_require}", String.valueOf(arena.data.encRequire.get(EnchantType.MULTIPLE))).replace("{level}",
                                lvl == null ? "0" : lvl)));
                    }
                    arena.replaceBlock(block, rs);
                    return;
                }
            }
        }
        double i2 = i1 - ((int) i1);
        int integer = ((int)i1) + (arena.b(i2) ? 1 : 0);
            if (arena.data.ciBlockDropChance.containsKey(mn)) {
            CIDropChance cidic = arena.a(arena.data.ciBlockDropChance.get(mn),deviation);
         //   double finalDeviation = deviation;
         //   Bukkit.getScheduler().runTask(DGCore.getInstance(), () -> miner.sendMessage(String.valueOf(finalDeviation)));
            if (cidic != null && !Utils.isInvFull(miner.getInventory()))
                miner.getInventory().addItem(ItemData.stringToIs(cidic.data));
//            List<CIDropChance> defaultDrop = new ArrayList<>();
//            for (CIDropChance ciDropChance : arena.data.ciBlockDropChance.get(mn)) {
//                if (ciDropChance.default_drop) defaultDrop.add(ciDropChance);
//            }
                if (arena.data.default_drop != null) {
                    ItemStack is = ItemData.stringToIs(arena.data.default_drop);
                    for (int i = 0; i < integer; i++) {
                        if (!Utils.isInvFull(miner.getInventory())) miner.getInventory().addItem(is);
                    }
                }
//            for (CIDropChance ciDropChance : defaultDrop) {
//                ItemStack is = ItemData.stringToIs(ciDropChance.data);
////                if (dgPlayer.playerUpgrades.containsKey(EnumUpgradeType.MULTIPLIER)) {
////                    int lvl = dgPlayer.playerUpgrades.get(EnumUpgradeType.MULTIPLIER);
////                    double value = PlayerUpgrade.upgrades.get(EnumUpgradeType.MULTIPLIER).value.get(lvl);
////                    int i1 = ((int) value);
////                    double i2 = value - i1;
////                    for (int i = 0; i < i1 + (arena.b(i2) ? 1 : 0); i++) {
////                        if (!Utils.isInvFull(miner.getInventory())) miner.getInventory().addItem(is);
////                    }
////                }
//                for (int i = 0; i < integer; i++) {
//                    if (!Utils.isInvFull(miner.getInventory())) miner.getInventory().addItem(is);
//                }
//            }
        }
        if (dgPlayer.playerUpgrades.containsKey(EnumUpgradeType.TOKEN)) {
            int lvl = dgPlayer.playerUpgrades.get(EnumUpgradeType.TOKEN);
            double value = Double.parseDouble(PlayerUpgrade.upgrades.get(EnumUpgradeType.TOKEN).getValue(lvl));
            if (arena.b(value / 100)) {
                TMAPI.setTokens(miner, (int) (TMAPI.getTokens(miner) + 1));
            }
        }
        if (arena.data.regenMode.equals(RegenMode.PER_BLOCK)) {
            Bukkit.getScheduler().runTask(DGCore.getInstance(), () -> {
                if (arena.data.block_waiting != null) {
                    assert arena.data.block_waiting.parseMaterial() != null;
                    block.setType(arena.data.block_waiting.parseMaterial());
                }
            });
        }
        int v1 = PlayerLevel.getMine(Integer.sum(dgPlayer.level,1));
        int v2 = PlayerLevel.getExp(Integer.sum(dgPlayer.level,1));
        if (dgPlayer.mined < v1) {
            if (dgPlayer.mined + integer < v1) {
                dgPlayer.mined += integer;
            } else {
                dgPlayer.mined = v1;
            }
            Integer i = dgPlayer.mined_in_region.get(arena.id);
            dgPlayer.mined_in_region.replace(arena.id, i, Integer.sum(i, 1));
        }
        int i = arena.data.expDrop.get(SelectionGUI.materialConverterBA(material).name());
        if (dgPlayer.exp < v2) {
            if (dgPlayer.exp + i > v2) {
                dgPlayer.exp = v2;
            } else {
                dgPlayer.exp += i;
            }
        }
        int i3 = Integer.parseInt(PlaceholderAPI.setPlaceholders(miner,"%core_nextLvl_block%"));
        int i4 = Integer.parseInt(PlaceholderAPI.setPlaceholders(miner,"%core_nextLvl_exp%"));
        if (i3 == 0 && i4 == 0)
            dgPlayer.level++;
        dgPlayer.lm = DGCore.getInstance().block_name_file.config.contains("name." + rs.name()) ?
                DGCore.getInstance().block_name_file.config.getString("name." + rs.name()) : rs.name();
        dgPlayer.lma = integer;
        dgPlayer.l = System.currentTimeMillis() + 2000;
        miner.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(Utils.applyColor(
                PlaceholderAPI.setPlaceholders(miner,
                        DGCore.getInstance().getConfig().getString("action_bar"))
                        .replace("{amount}","x" + dgPlayer.lma)
                        .replace("{name}",dgPlayer.lm)
        )));
    }

    private boolean b(double c) {
        return Math.random() < c;
    }

    private CIDropChance a(List<CIDropChance> list,double deviation) {
        double d = Math.random();
        for (CIDropChance item : list) {
            double chance = (item.chance * deviation) / 100;
            if (d < chance) return item;
        }
        return null;
    }


//    public boolean checkItemRequire(ItemStack itemStack) {
//        EnumType selected = data.enc;
//        int require = data.encRequire.get(selected);
//        if (require == 0) return true;
//        if (!DGCore.getInstance().nmsManager.nbtStorage().contain(itemStack,"dgcore:" + id.toLowerCase())) return false;
//        int value = Integer.parseInt(DGCore.getInstance().nmsManager.nbtStorage().get(itemStack,"dgcore:" + id.toLowerCase()));
//        return value >= require;
//    }

    public static void bossBarUpdater() {
        new Thread(() -> {
            while (DGCore.getInstance().isEnabled()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        for (Arena arena : DGCore.getInstance().arenas.values()) {
                            if (!arena.data.pos1.w.equals(player.getLocation().getWorld().getName())) continue;
                            if (Utils.insideBox(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(),
                                    arena.data.pos1.x, arena.data.pos1.y, arena.data.pos1.z, arena.data.pos2.x, arena.data.pos2.y, arena.data.pos2.z, arena.data.bbd)) {
                                if (arena.players.contains(player)) continue;
                                arena.players.add(player);
                            } else {
                                if (!arena.players.contains(player)) continue;
                                arena.players.remove(player);
                            }
                        }
                    }
                    for (Arena arena : DGCore.getInstance().arenas.values()) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            DGPlayer dgPlayer = DGCore.getInstance().dataSerialize.player.get(player.getUniqueId().toString());
                            if (arena.players.contains(player)) {
                                arena.removeBossBar(dgPlayer);
                                arena.sendBossBar(dgPlayer);
                            } else {
                                arena.removeBossBar(dgPlayer);
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

    public static void topUpdater() {
        new Thread(() -> {
            while (DGCore.getInstance().isEnabled()) {
                    Map<DGPlayer, Integer> map = new HashMap<>();
                    for (Arena arena : DGCore.getInstance().arenas.values()) {
                        Map<DGPlayer, Integer> hashMap = new HashMap<>();
                        for (DGPlayer dgPlayer : DGCore.getInstance().dataSerialize.player.values()) {
                            hashMap.put(dgPlayer, dgPlayer.mined_in_region.get(arena.id));
                            map.put(dgPlayer, dgPlayer.mined_in_region.get(arena.id));
                        }
                        List<Map.Entry<DGPlayer, Integer>> sort = Arena.sort(hashMap);
                        Map<Integer, DGPlayer> result = new HashMap<>();
                        int i = 1;
                        for (Map.Entry<DGPlayer, Integer> entry : sort) {
                            if (entry.getKey().top.containsKey(arena.id)) {
                                entry.getKey().top.replace(arena.id, i);
                            } else {
                                entry.getKey().top.put(arena.id, i);
                            }
                            result.put(i, entry.getKey());
                            i++;
                        }
                        arena.topPlayer = result;
                    }
                    List<Map.Entry<DGPlayer, Integer>> sorted = Arena.sort(map);
                    Map<Integer, DGPlayer> rs = new HashMap<>();
                    int j = 1;
                    for (Map.Entry<DGPlayer, Integer> entry : sorted) {
                        rs.put(j, entry.getKey());
                        j++;
                    }
                    Arena.top_Player = rs;
                    Map<DGPlayer, Integer> hashMap = new HashMap<>();
                    for (DGPlayer dgPlayer : DGCore.getInstance().dataSerialize.player.values()) {
                        int i = 0;
                        for (int l : dgPlayer.mined_in_region.values())
                            i += l;
                        hashMap.put(dgPlayer, i);
                    }

                    List<Map.Entry<DGPlayer, Integer>> sort = Arena.sort(hashMap);
                    Map<Integer, DGPlayer> result = new HashMap<>();
                    int i = 1;
                    for (Map.Entry<DGPlayer, Integer> entry : sort) {
                        entry.getKey().top2 = i;
                        result.put(i, entry.getKey());
                        i++;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        }).start();
    }

    private static List<Map.Entry<DGPlayer,Integer>> sort(Map<DGPlayer,Integer> map) {
        return map.entrySet().stream().sorted(Map.Entry.<DGPlayer, Integer>comparingByValue().reversed()).toList();
    }

    public void sendBossBar(DGPlayer player) {
        Bukkit.getScheduler().runTask(DGCore.getInstance(), () -> {
            OfflinePlayer p = Objects.requireNonNull(Bukkit.getPlayer(player.uuid));
            Arena.bossBar.remove(player);

            Arena.bossBar.put(player,Bukkit.createBossBar(Utils.applyColor(PlaceholderAPI.setPlaceholders(p,DGCore.getInstance().getConfig().getString("boss_bar"))
                    .replace("{amount}",player.lma != -1 ? "x"+player.lma : "")
                    .replace("{name}",player.lm != null ? player.lm : "")), BarColor.WHITE, BarStyle.SOLID));
            if (p.isOnline())
                Arena.bossBar.get(player).addPlayer(p.getPlayer());
        });
    }

    public void removeBossBar(DGPlayer player) {
        Bukkit.getScheduler().runTask(DGCore.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (Arena.bossBar.containsKey(player)) {
                    Arena.bossBar.get(player).removeAll();
                }
                Arena.bossBar.remove(player);
            }
        });
    }

}
//    private Map<Integer, BlockChance> chance = new HashMap<>();
//
//    private Material luckyBlock() {
//        if (chance.size() == 0 && data.blockChances.size() > 0)
//            for (BlockChance bc : data.blockChances)
//                for (int i = chance.size(); i < chance.size() + bc.chance; i++)
//                    if (i > 100) break;
//                    else
//                        chance.put(i, bc);
//        int luckyNumber = Utils.random(0, 100);
//        BlockChance bc = chance.get(luckyNumber);
//        if (bc == null) bc = chance.get(69);
//        return XMaterial.valueOf(bc.material).parseMaterial();
//    }