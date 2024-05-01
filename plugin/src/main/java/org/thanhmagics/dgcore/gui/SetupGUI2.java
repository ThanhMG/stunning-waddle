package org.thanhmagics.dgcore.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.thanhmagics.DGCore;
import org.thanhmagics.dgcore.Arena;
import org.thanhmagics.dgcore.DGPlayer;
import org.thanhmagics.dgcore.EnchantType;
import org.thanhmagics.dgcore.RegenMode;
import org.thanhmagics.dgcore.arena.CIDropChance;
import org.thanhmagics.dgcore.listener.ChatListener;
import org.thanhmagics.dgcore.listener.PacketReader;
import org.thanhmagics.utils.ItemBuilder;
import org.thanhmagics.utils.ItemData;
import org.thanhmagics.utils.Utils;
import org.thanhmagics.utils.XMaterial;

import java.util.*;

public class SetupGUI2 implements Listener {

    public static Map<UUID, SetupGUI2> guis = new HashMap<>();

    public Arena arena;

    public void open(Player player, Arena arena) {
        DGPlayer dgPlayer = DGCore.getInstance().dataSerialize.player.get(player.getUniqueId().toString());
        this.arena = arena;
        UUID uuid = UUID.randomUUID();
        guis.put(uuid, this);
        Inventory inventory = Bukkit.createInventory(null, 9 * 3, "Edit: " + arena.id);

        inventory.setItem(9, new ItemBuilder(XMaterial.EXPERIENCE_BOTTLE.parseMaterial())
                .setDisplayName("&aEXP Drop")
                .addLore("")
                .addLore("&eClick Để Xem Thêm")
                .build());

        inventory.setItem(10, new ItemBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODVmYTBhM2EyZDYyZDdkMTE3MWQ0OGIzYWU4ZmNiNTUxZjZmYWNjYzcwY2VlNDBmNDRjNzY3YzNkYTdiNzg1ZiJ9fX0=")
                .setDisplayName("&aRegen Mode")
                .addLore("")
                .addLore((arena.data.regenMode.equals(RegenMode.PER_BLOCK) ? "&a▸" : "&7") + "PER_BLOCK &7(mỗi block có cooldown riêng)")
                .addLore((arena.data.regenMode.equals(RegenMode.TIME) ? "&a▸" : "&7") + "TIME &7(regen tất cả block sau 1 khoảng thời gian)")
                .addLore((arena.data.regenMode.equals(RegenMode.EMPTY) ? "&a▸" : "&7") + "EMPTY &7(regen tất cả block khi đã đào hết)")
                .addLore("")
                .addLore(arena.data.regenMode.equals(RegenMode.TIME) ? "&7Giá Trị Đã Chọn:&6 " + (arena.data.regenCD_time / 1000) + "s" : (
                        arena.data.regenMode.equals(RegenMode.PER_BLOCK) ? "&7Cooldown:&6 " + arena.data.regenCD_per_block.values().toArray()[0] : ""
                ))
                .addLore("")
                .addLore(arena.data.regenMode.equals(RegenMode.TIME) ? "&eRight-Click Để Thay Đổi Giá Trị" : arena.data.regenMode.equals(RegenMode.PER_BLOCK) ? "&eRight-Click Để Thay Đổi Giá Trị" : "")
                .addLore("&eLeft-Click Vào Để Thay Đổi RegenMode")
                .build());

        inventory.setItem(11, new ItemBuilder(XMaterial.ENCHANTED_BOOK.parseMaterial())
                .setDisplayName("&aYêu Cầu Enchant")
                .addLore("&7- Chỉ Sử Dụng Item Đã Enc Mới Đào Được Trong Region")
                .addLore("")
                .addLore("&7Level Require Cho MULTIPLIER: &6" + arena.data.encRequire.get(EnchantType.MULTIPLE))
                .addLore("&7Level Require Cho DROP: &6" + arena.data.encRequire.get(EnchantType.DROP))
                .addLore("")
                .addLore("&eLEFT-CLICK Để Thay Đổi Giá Trị MULTIPLIER")
                .addLore("&eRIGHT-CLICK Để Thay Đổi Giá Trị DROP")
                .build());

        inventory.setItem(12, new ItemBuilder(XMaterial.DIAMOND.parseMaterial())
                .setDisplayName("&aPlayer Level Require")

                .addLore("&7- Đã Chọn:&6 " + arena.data.levelRequire)

                .addLore("")
                .addLore("&eClick Vào Đây Để Thay Đổi")
                .build());
        inventory.setItem(13, new ItemBuilder(XMaterial.IRON_BARS.parseMaterial())
                .setDisplayName("&aRequire Permission")
                .addLore("&7- Permission không bắt buộc phải có core.mined!")
                .addLore("")
                .addLore("&7- Đã Chọn: &6" + arena.data.permission)
                .addLore("")
                .addLore("&eClick Vào Để Thay Đổi")
                .build());

        inventory.setItem(14, new ItemBuilder(XMaterial.ENDER_DRAGON_SPAWN_EGG.parseMaterial())
                .setDisplayName("&aBossBar Display")
                .addLore("&7Player Đứng Gần Region Bao Nhiêu Block Sẽ Hiện BossBar")
                .addLore("")
                .addLore("&7- Khoảng Cách: &6" + arena.data.bbd + " block")
                .addLore("")
                .addLore("&eClick Để Thay Đổi Giá Trị")
                .build());
        inventory.setItem(15, new ItemBuilder(arena.data.block_waiting != null ? arena.data.block_waiting.parseMaterial() : XMaterial.BARRIER.parseMaterial())
                .setDisplayName("&aBlock Chờ")
                .addLore("")
                .addLore("&7- Kích Hoạt: &d" + (arena.data.block_waiting != null ? "True" : "False"))
                .addLore((arena.data.block_waiting != null ? "&7- Đã Chọn: &6" + arena.data.block_waiting.parseMaterial().name() : ""))
                .addLore("")
                .addLore(arena.data.block_waiting != null ? "&eLeft-Click Để Thay Đổi Block Đã Chọn" : "")
                .addLore("&eRight-Click Để Bật")
                .build());

        inventory.setItem(16, new ItemBuilder(XMaterial.EMERALD.parseMaterial())
                .setDisplayName("&aCustom Drop")
                .addLore("")
                .addLore("&eClick Để Xem Thêm")
                .build());
//        ItemStack is = arena.data.default_drop != null ? ItemData.stringToIs(arena.data.default_drop) : null;
//        inventory.setItem(17, new ItemBuilder(is != null ? is.getType() : XMaterial.BARRIER.parseMaterial())
//                .setDisplayName("&aDefault Drop")
//                .addLore("")
//                .addLore("&7- Đã Chọn:&f " + (is != null ? ItemBuilder.getDisplayName(is) : "&cKhông Có"))
//                .addLore("")
//                .addLore("&eClick Vào Item Trong Inv Để Set Làm Default-Drop")
//                .setGlow(is != null && (is.getEnchantments().size() > 0)).setAmount(is != null ? is.getAmount() : 1).build());
        player.openInventory(inventory);
        dgPlayer.inv = uuid;

    }

    ItemStack pplus = new ItemBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIzMzJiNzcwYTQ4NzQ2OTg4NjI4NTVkYTViM2ZlNDdmMTlhYjI5MWRmNzY2YjYwODNiNWY5YTBjM2M2ODQ3ZSJ9fX0=")
            .setDisplayName("&aThêm Drop")
            .addLore("")
            .addLore("&eClick Vào Item Trong Túi Đồ")
            .build();

    @EventHandler
    public void onEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        DGPlayer dgPlayer = DGCore.getInstance().dataSerialize.player.get(player.getUniqueId().toString());
        if (dgPlayer.inv == null) return;
        if (!guis.containsKey(dgPlayer.inv)) return;
        int slot = event.getSlot();
        event.setCancelled(true);
        Arena arena = guis.get(dgPlayer.inv).arena;
        if (arena == null) {
            player.closeInventory();
            return;
        }
        if (!(event.getClickedInventory() instanceof PlayerInventory)) {
            if (slot == 9) {
                new SelectionGUI(new SelectionGUI.SG_Content() {
                    @Override
                    public List<ItemStack> get() {
                        List<ItemStack> ct = new ArrayList<>();
                        List<Material> materials = new ArrayList<>();
                        for (String l : arena.data.region.values()) {
                            Material m = XMaterial.matchXMaterial(l).get().parseMaterial();
                            if (!materials.contains(m))
                                materials.add(m);
                        }
                        for (Material material : materials) {
                            ItemStack ib = new ItemBuilder(SelectionGUI.materialConverterAB(material))
                                    .setDisplayName("&f" + material.name().toLowerCase())
                                    .addLore("").addLore("&7- EXP Drop:&6 " + arena.data.expDrop.get(material.name()))
                                    .addLore("")
                                    .addLore("&eClick Để Chỉnh EXP Drop Cho Block")
                                    .build();
                            ct.add(ib);
                        }
                        return ct;
                    }
                }, 54, "EXP Drop Edit", new SelectionGUI.SG_OnClick() {
                    @Override
                    public void run(ItemStack clicked, int stt, ClickType clickType, SelectionGUI instance) {
                        ChatListener.add(player, new ChatListener.Runnable() {
                            @Override
                            public void run(PlayerChatEvent event) {
                                if (event.getMessage().equalsIgnoreCase("cancel")) {
                                    instance.openGUI(dgPlayer, 1);
                                    return;
                                }
                                try {
                                    int i = Integer.parseInt(event.getMessage());
                                    int old = arena.data.expDrop.get(SelectionGUI.materialConverterBA(clicked.getType()).name());
                                    arena.data.expDrop.replace(SelectionGUI.materialConverterBA(clicked.getType()).name(), old, i);
                                    instance.openGUI(dgPlayer, instance.page);
                                } catch (Exception e) {
                                    player.sendMessage(Utils.applyColor("&cKhong The Nhan Dang So: " + e.getMessage()));
                                }
                            }
                        });
                    }
                }, null, () -> new SetupGUI2().open(player, arena)).openGUI(dgPlayer, 1);
            } else if (event.getSlot() == 10) {
                if (event.getClick().equals(ClickType.LEFT)) {
                    if (arena.data.regenMode.equals(RegenMode.PER_BLOCK)) {
                        arena.data.regenMode = RegenMode.TIME;
                    } else if (arena.data.regenMode.equals(RegenMode.TIME)) {
                        arena.data.regenMode = RegenMode.EMPTY;
                    } else {
                        arena.data.regenMode = RegenMode.PER_BLOCK;
                    }
                    new SetupGUI2().open(player, arena);
                } else if (event.getClick().equals(ClickType.RIGHT)) {
                    if (arena.data.regenMode.equals(RegenMode.TIME)) {
                        ChatListener.add(player, new ChatListener.Runnable() {
                            @Override
                            public void run(PlayerChatEvent event) {
                                if (event.getMessage().equalsIgnoreCase("cancel")) {
                                    new SetupGUI2().open(player, arena);
                                    return;
                                }
                                try {
                                    int value = Integer.parseInt(event.getMessage());
                                    if (value > 100000) {
                                        player.sendMessage(Utils.applyColor("&cSố Quá Lớn@@"));
                                        player.closeInventory();
                                        return;
                                    }
                                    arena.data.regenCD_time = (value * 1000);
                                    arena.regenRegion = System.currentTimeMillis();
                                    new SetupGUI2().open(player, arena);
                                } catch (Exception e) {
                                    player.sendMessage(Utils.applyColor("&cKhông Thể Nhận Dạng '" + event.getMessage() + "' Là Số Nào!"));
                                }
                            }
                        });
                    } else if (arena.data.regenMode.equals(RegenMode.PER_BLOCK)) {
                        ChatListener.add(player, new ChatListener.Runnable() {
                            @Override
                            public void run(PlayerChatEvent event) {
                                if (event.getMessage().equalsIgnoreCase("cancel")) {
                                    new SetupGUI2().open(player, arena);
                                    return;
                                }
                                try {
                                    int value = Integer.parseInt(event.getMessage());
                                    if (value > 100000) {
                                        player.sendMessage(Utils.applyColor("&cSố Quá Lớn@@"));
                                        player.closeInventory();
                                        return;
                                    }
                                    Map<String, Integer> map = new HashMap<>();
                                    for (String s : arena.data.regenCD_per_block.keySet()) {
                                        map.put(s, value);
                                    }
                                    arena.data.regenCD_per_block = map;
                                    new SetupGUI2().open(player, arena);
                                } catch (Exception e) {
                                    player.sendMessage(Utils.applyColor("&cKhông Thể Nhận Dạng '" + event.getMessage() + "' Là Số Nào!"));
                                }
                            }
                        });
                    }
                }
            } else if (event.getSlot() == 11) {
                ChatListener.add(player, new ChatListener.Runnable() {
                    @Override
                    public void run(PlayerChatEvent e) {
                        if (e.getMessage().equalsIgnoreCase("cancel")) {
                            new SetupGUI2().open(player, arena);
                            return;
                        }
                        try {
                            int level = Integer.parseInt(e.getMessage());
                            if (event.getClick().equals(ClickType.LEFT)) {
                                arena.data.encRequire.replace(EnchantType.MULTIPLE, level);
                            } else if (event.getClick().equals(ClickType.RIGHT)) {
                                arena.data.encRequire.replace(EnchantType.DROP, level);
                            }
                            new SetupGUI2().open(player, arena);
                        } catch (Exception exc) {
                            player.sendMessage(Utils.applyColor("Không Thể Nhận Diện Số: " + e.getMessage()));
                        }
                    }
                });
            } else if (event.getSlot() == 12) {
                ChatListener.add(player, new ChatListener.Runnable() {
                    @Override
                    public void run(PlayerChatEvent event) {
                        if (event.getMessage().equalsIgnoreCase("cancel")) {
                            new SetupGUI2().open(player, arena);
                            return;
                        }
                        arena.data.levelRequire = Integer.parseInt(event.getMessage());
                        new SetupGUI2().open(player, arena);
                    }
                });
            } else if (event.getSlot() == 13) {
                ChatListener.add(player, new ChatListener.Runnable() {
                    @Override
                    public void run(PlayerChatEvent event) {
                        if (event.getMessage().equalsIgnoreCase("cancel")) {
                            new SetupGUI2().open(player, arena);
                            return;
                        }
                        arena.data.permission = event.getMessage();
                        new SetupGUI2().open(player, arena);
                    }
                });
            } else if (event.getSlot() == 14) {
                ChatListener.add(player, new ChatListener.Runnable() {
                    @Override
                    public void run(PlayerChatEvent event) {
                        if (event.getMessage().equalsIgnoreCase("cancel")) {
                            new SetupGUI2().open(player, arena);
                            return;
                        }
                        try {
                            arena.data.bbd = Integer.parseInt(event.getMessage());
                            new SetupGUI2().open(player, arena);
                        } catch (Exception exc) {
                            player.sendMessage(Utils.applyColor("Không Thể Nhận Diện Số: " + event.getMessage()));
                        }
                    }
                });
            } else if (event.getSlot() == 15) {
                if (event.getClick().equals(ClickType.LEFT)) {
                    ChatListener.add(player, new ChatListener.Runnable() {
                        @Override
                        public void run(PlayerChatEvent event) {
                            if (event.getMessage().equalsIgnoreCase("cancel")) {
                                new SetupGUI2().open(player, arena);
                                return;
                            }
                            String str = event.getMessage();
                            Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(str);
                            if (xMaterial.isPresent()) {
                                arena.data.block_waiting = xMaterial.get();
                                new SetupGUI2().open(player, arena);
                            } else {
                                player.sendMessage(Utils.applyColor("&cMaterial Không Hợp Lệ"));
                            }
                        }
                    });
                } else if (event.getClick().equals(ClickType.RIGHT)) {
                    if (arena.data.block_waiting != null) {
                        arena.data.block_waiting = null;
                    } else {
                        arena.data.block_waiting = XMaterial.BEDROCK;
                    }
                    new SetupGUI2().open(player, arena);
                }
            } else if (event.getSlot() == 16) {
                new SelectionGUI(new SelectionGUI.SG_Content() {
                    @Override
                    public List<ItemStack> get() {
                        List<ItemStack> itemStacks = new ArrayList<>();
                        for (String s : arena.data.ciBlockDropChance.keySet()) {
                            itemStacks.add(new ItemBuilder(Material.valueOf(s.toUpperCase()))
                                    .setDisplayName(s)
                                    .addLore("")
                                    .addLore("&eClick Để Thay Đổi")
                                    .build());
                        }
                        return itemStacks;
                    }
                }, 54, "Custom Drop", new SelectionGUI.SG_OnClick() {
                    @Override
                    public void run(ItemStack clickedd, int stt, ClickType clickType, SelectionGUI instance) {
                        new SelectionGUI(new SelectionGUI.SG_Content() {
                            @Override
                            public List<ItemStack> get() {
                                List<ItemStack> is = new ArrayList<>();
                                for (CIDropChance ciDropChance : arena.data.ciBlockDropChance.get(clickedd.getType().name())) {
                                    is.add(new ItemBuilder(ItemData.stringToIs(ciDropChance.data).clone())
                                            .addLore("")
                                            .addLore("&e------------------")
                                            .addLore("&eDrop Chance:&6 " + ciDropChance.chance + "%")
                                            .addLore("")
                                            .addLore("&eLEFT-CLICK Để Chỉnh Drop-Chance")
                                            .addLore("&eRIGHT-CLICK Để Xóa")
                                            .addLore("&e------------------").build());
                                }
                                is.add(pplus);
                                return is;
                            }
                        }, 54, "Custom Drop: " + clickedd.getType().name(), new SelectionGUI.SG_OnClick() {
                            @Override
                            public void run(ItemStack clicked, int stt, ClickType clickType, SelectionGUI instance) {
                                if (ItemBuilder.simpleEqual(pplus, clicked)) return;
                                int i = (stt - 9) + ((instance.size - 18) * (instance.page - 1));
                                List<CIDropChance> list = arena.data.ciBlockDropChance.get(clickedd.getType().name());
                                CIDropChance current = arena.data.ciBlockDropChance.get(clickedd.getType().name()).get(i);
                                if (clickType.equals(ClickType.LEFT)) {
                                    ChatListener.add(player, new ChatListener.Runnable() {
                                        @Override
                                        public void run(PlayerChatEvent event) {
                                            if (event.getMessage().equalsIgnoreCase("cancel")) {
                                                instance.openGUI(dgPlayer, 1);
                                                return;
                                            }
                                            try {
                                                double chance = Integer.parseInt(event.getMessage());
                                                double j = 0;
                                                for (CIDropChance c2 : list)
                                                    j += c2.chance;
                                                if (j + chance - current.chance > 100) {
                                                    player.sendMessage(Utils.applyColor("&cTổng Số Tỉ Lệ Đã Lớn Hơn 100!"));
                                                    return;
                                                }
                                                current.chance = chance;
                                                instance.openGUI(dgPlayer, instance.page);
                                            } catch (Exception e) {
                                                player.sendMessage(Utils.applyColor("&c Không thể nhân diện số: " + event.getMessage()));
                                            }
                                        }
                                    });
                                } else if (clickType.equals(ClickType.RIGHT)) {
                                    list.remove(current);
                                    instance.openGUI(dgPlayer, instance.page);
                                }
                            }
                        }, new SelectionGUI.SG_OnClick() {
                            @Override
                            public void run(ItemStack clicked, int stt, ClickType clickType, SelectionGUI instance) {
                                List<CIDropChance> list = arena.data.ciBlockDropChance.get(clickedd.getType().name());
                                if (clicked != null && !clicked.getType().equals(Material.AIR)) {
                                    list.add(new CIDropChance(ItemData.isToString(clicked), 0.0));
                                    instance.openGUI(dgPlayer, instance.page);
                                }
                            }
                        }, () -> new SetupGUI2().open(player, arena)).openGUI(dgPlayer, 1);
                    }
                }, null, () -> new SetupGUI2().open(player, arena)).openGUI(dgPlayer, 1);
            }
        } else {
//            if (event.getCurrentItem() != null) {
//                arena.data.default_drop = ItemData.isToString(event.getCurrentItem());
//                new SetupGUI2().open(player,arena);
//            }
        }
    }
}
