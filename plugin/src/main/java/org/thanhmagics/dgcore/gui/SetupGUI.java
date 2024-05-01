package org.thanhmagics.dgcore.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.thanhmagics.dgcore.Arena;
import org.thanhmagics.dgcore.DGPlayer;
import org.thanhmagics.DGCore;
import org.thanhmagics.dgcore.EnchantType;
import org.thanhmagics.dgcore.RegenMode;
import org.thanhmagics.dgcore.arena.CIDropChance;
import org.thanhmagics.dgcore.listener.PacketReader;
import org.thanhmagics.utils.ItemBuilder;
import org.thanhmagics.utils.ItemData;
import org.thanhmagics.utils.Utils;
import org.thanhmagics.utils.XMaterial;

import java.util.*;

public class SetupGUI implements Listener {

    public static Map<UUID, SetupGUI> guis = new HashMap<>();

    public Arena arena;

    private void open(Player player, Arena arena) {
        this.arena = arena;
        DGPlayer dgPlayer = DGCore.getInstance().dataSerialize.player.get(player.getUniqueId().toString());
        UUID uuid = UUID.randomUUID();
        Inventory inventory = Bukkit.createInventory(null, 9 * 3, "Setup GUI: " + arena.id);
        inventory.setItem(10, new ItemBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQ5YjcxMmNkZjkxNDc3NTllYmE0ZDU1NzNjZGI2MzgzM2I0NGYzYWNlNGZjYjY2ZWEzYWIxNDcyMDdkYTNmZiJ9fX0=")
                .setDisplayName("&aChỉnh Sửa Block")
                //  .addLore("")
                .addLore("&7- thời gian đào của block")
                .addLore("&7- exp drop của block")
                .addLore("")
                .addLore("&eClick Vào Đây Để Thay Đổi")
                .build());
        inventory.setItem(11, new ItemBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODVmYTBhM2EyZDYyZDdkMTE3MWQ0OGIzYWU4ZmNiNTUxZjZmYWNjYzcwY2VlNDBmNDRjNzY3YzNkYTdiNzg1ZiJ9fX0=")
                .setDisplayName("&aRegen Mode")
                .addLore("")
                .addLore((arena.data.regenMode.equals(RegenMode.PER_BLOCK) ? "&a▸" : "&7") + "PER_BLOCK &7(mỗi block có cooldown riêng)")
                .addLore((arena.data.regenMode.equals(RegenMode.TIME) ? "&a▸" : "&7") + "TIME &7(regen tất cả block sau 1 khoảng thời gian)")
                .addLore((arena.data.regenMode.equals(RegenMode.EMPTY) ? "&a▸" : "&7") + "EMPTY &7(regen tất cả block khi đã đào hết)")
                .addLore("")
                .addLore(arena.data.regenMode.equals(RegenMode.TIME) ? "&7Giá Trị Đã Chọn:&6 " + (arena.data.regenCD_time / 1000) + "s" : "")
                .addLore("")
                .addLore(arena.data.regenMode.equals(RegenMode.TIME) ? "&eRight-Click Để Thay Đổi Giá Trị" : "")
                .addLore("&eLeft-Click Vào Để Thay Đổi RegenMode")
                .build());

        inventory.setItem(12, new ItemBuilder(XMaterial.ENCHANTED_BOOK.parseMaterial())
                .setDisplayName("&aYêu Cầu Enchant")
                .addLore("&7- Chỉ Sử Dụng Item Đã Enc Mới Đào Được Trong Region")
                .addLore("")
                .addLore("&7Level Require Cho MULTIPLIER: &6" + arena.data.encRequire.get(EnchantType.MULTIPLE))
                .addLore("&7Level Require Cho DROP: &6" + arena.data.encRequire.get(EnchantType.DROP))
                .addLore("")
                .addLore("&eLEFT-CLICK Để Thay Đổi Giá Trị MULTIPLIER")
                .addLore("&eRIGHT-CLICK Để Thay Đổi Giá Trị DROP")
                .build());
//        lore.addAll(Utils.stringList(arena.enchantRequire, 30));
//        inventory.setItem(12, new ItemBuilder(XMaterial.ENCHANTED_BOOK.parseMaterial()).setGlow(true)
//                .setDisplayName("&aYêu Cầu Enchant")
//                .addLore("")
//                .addLore(lore)
//                .addLore("")
//                .addLore("&eClick Vào Để Thay Đổi")
//                .build());
//        lore.clear();
//        for (CIDropChance ci : arena.data.ciDropChances)
//            lore.add("&7-&f "+ItemBuilder.getDisplayName(ItemData.stringToIs(ci.data)) + " &6(" + ci.chance + "%)");
        inventory.setItem(13, new ItemBuilder(XMaterial.DIAMOND.parseMaterial())
                .setDisplayName("&aPlayer Level Require")
                // .addLore("")
                //  .addLore(Utils.stringList(lore, 30))
                .addLore("&7- Đã Chọn:&6 " + arena.data.levelRequire)

                .addLore("")
                .addLore("&eClick Vào Đây Để Thay Đổi")
                .build());
        inventory.setItem(14, new ItemBuilder(XMaterial.IRON_BARS.parseMaterial())
                .setDisplayName("&aRequire Permission")
                .addLore("&7- Permission không bắt buộc phải có core.mined!")
                .addLore("")
                .addLore("&7- Đã Chọn: &6" + arena.data.permission)
                .addLore("")
                .addLore("&eClick Vào Để Thay Đổi")
                .build());
        //   lore.clear();
//        for (String key : arena.ciBlockDropChance.keySet()) {
//            StringBuilder s = new StringBuilder("&f" + key + ": ");
//            int l = key.length();
//            boolean b = false;
//            for (CIDropChance value : arena.ciBlockDropChance.get(key)) {
//                if (!b) {
//                    s.append(ItemBuilder.getDisplayName(ItemData.stringToIs(value.data)));
//                    s.append("&6(").append(value.chance).append("%)");
//                    b = true;
//                    lore.add(s.toString());
//                } else {
//                    StringBuilder sb = new StringBuilder();
//                    for (int i = 0; i < l; i++)
//                        sb.append(" ");
//                    sb.append("  ");
//                    sb.append(ItemBuilder.getDisplayName(ItemData.stringToIs(value.data)));
//                    sb.append("&6(").append(value.chance).append("%)");
//                    lore.add(sb.toString());
//                }
//            }
//        }
        inventory.setItem(15, new ItemBuilder(XMaterial.EMERALD.parseMaterial())
                .setDisplayName("&aCustom Drop (Tùy Block)")
                .addLore("&7- Khi Đào Block Sẽ Không Drop Ra Những Item Cơ Bản Của Minecraft")
                .addLore("&7  Thay Vào Đó Sẽ Drop Ra Những Thứ Này")
                .addLore("&7- Default Drop: chắc chắn drop ra và là item đc multiple")
                //   .addLore("")
                //  .addLore(lore)
                .addLore("")
                .addLore("&eClick Vào Để Thay Đổi")
                .build());

        inventory.setItem(16, new ItemBuilder(XMaterial.ENDER_DRAGON_SPAWN_EGG.parseMaterial())
                .setDisplayName("&aBossBar Display")
                .addLore("&7Player Đứng Gần Region Bao Nhiêu Block Sẽ Hiện BossBar")
                .addLore("")
                .addLore("&7- Khoảng Cách: &6" + arena.data.bbd + " block")
                .addLore("")
                .addLore("&eClick Để Thay Đổi Giá Trị")
                .build());
        inventory.setItem(17, new ItemBuilder(arena.data.block_waiting != null ? arena.data.block_waiting.parseMaterial() : XMaterial.BARRIER.parseMaterial())
                .setDisplayName("&aBlock Chờ")
                .addLore("")
                .addLore("&7- Kích Hoạt: &d" + (arena.data.block_waiting != null ? "True" : "False"))
                .addLore((arena.data.block_waiting != null ? "&7- Đã Chọn: &6" + arena.data.block_waiting.parseMaterial().name() : ""))
                .addLore("")
                .addLore(arena.data.block_waiting != null ? "&eLeft-Click Để Thay Đổi Block Đã Chọn" : "")
                .addLore("&eRight-Click Để Bật")
                .build());

        player.openInventory(inventory);
        dgPlayer.inv = uuid;
        guis.put(uuid, this);
    }

    @EventHandler
    public void initListener(InventoryClickEvent event) {
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
//            if (slot == 10) {
//                new SelectionGUI(new SelectionGUI.SG_Content() {
//                    @Override
//                    public List<ItemStack> get() {
//                        List<ItemStack> ct = new ArrayList<>();
//                        for (String key : arena.data.regenCD.keySet()) {
//                            ItemBuilder ib = new ItemBuilder(SelectionGUI.materialConverterAB(XMaterial.valueOf((key.toUpperCase())).parseMaterial()));
//                            ib.setDisplayName("&f" + key.toLowerCase());
//                            ib.addLore("");
//                            ib.addLore("&7- regen cooldown: &6" + arena.data.regenCD.get(key) / 1000 + "s");
//                            ib.addLore(PacketReader.farm2.contains(XMaterial.matchXMaterial(ib.getMaterial())) ? "&7- Các Loại Cây Trồng Không Cần Tốc Độ Đào!" :
//                                    "&7- mining speed: &6" + arena.data.miningSpeed.get(key) / 1000  + "s");
//                            ib.addLore("").addLore("&eLEFT-LICK Để Thay Đổi Cooldown")
//                                    .addLore("&eRIGHT-CLICK Để Thay Đổi Tốc Độ Đào");
//                            ct.add(ib.build());
//                        }
//                        return ct;
//                    }
//                }, 54, "Block Edit", new SelectionGUI.SG_OnClick() {
//                    @Override
//                    public void run(ItemStack clicked, int stt, ClickType clickType, SelectionGUI instance) {
//                        if (clickType.equals(ClickType.LEFT)) {
//                            new SignGUI() {
//                                @Override
//                                public void onClose(Player player, List<String> lines) {
//                                    try {
//                                        int n = Integer.parseInt(lines.get(0));
//                                        if (n > 100000) {
//                                            player.sendMessage(Utils.applyColor("&cSố Quá Lớn@@"));
//                                            player.closeInventory();
//                                            return;
//                                        }
//                                        Material clickedType = SelectionGUI.materialConverterBA(clicked.getType());
//                                        int old = arena.data.regenCD.get(clickedType.name());
//                                        arena.data.regenCD.replace(clickedType.name(), old, n * 1000);
//                                        //    player.sendMessage(Utils.applyColor("&aThay Đổi Thành Công!&6 (" + old / 1000 + "->" + n + ")"));
//                                        instance.openGUI(dgPlayer, instance.page);
//                                    } catch (Exception e) {
//                                        player.sendMessage(Utils.applyColor("&cKhông Thể Nhận Dạng '" + lines.get(0) + "' Là Số Nào!"));
//                                    }
//                                }
//                            }.setLine(1, "^^^^^").setLine(2, "Đơn Vị: Giây")
//                                    .setLine(3, "(Chỉ Ghi Số!)").open(player);
//                        } else if (clickType.equals(ClickType.RIGHT)) {
//                            if (PacketReader.farm2.contains(XMaterial.matchXMaterial(clicked.getType()))) return;
//                            new SignGUI() {
//                                @Override
//                                public void onClose(Player player, List<String> lines) {
//                                    try {
//                                        int speed = Integer.parseInt(lines.get(0));
//                                        if (speed > 100000) {
//                                            player.sendMessage(Utils.applyColor("&cSố Quá Lớn@@"));
//                                            player.closeInventory();
//                                            return;
//                                        }
//                                        int old = arena.data.miningSpeed.get(clicked.getType().name());
//                                        arena.data.miningSpeed.replace(clicked.getType().name(), old, speed * 1000);
//                                        //  player.sendMessage(Utils.applyColor("&a Chỉnh Sửa Thành Công"));
//                                        instance.openGUI(dgPlayer, instance.page);
//                                    } catch (Exception e) {
//                                        player.sendMessage(Utils.applyColor("&cKhông Thể Nhận Dạng '" + lines.get(0) + "' Là Số Nào!"));
//                                    }
//                                }
//                            }.setLine(1, "^^^^^").setLine(2, "Tốc Độ Đào").open(player);
//                        }
//                    }
//                }, null, () -> new SetupGUI().open(player, arena)).openGUI(dgPlayer, 1);
//            } else if (slot == 11) {
//                new SignGUI() {
//                    @Override
//                    public void onClose(Player player, List<String> lines) {
//                        try {
//                            int n = Integer.parseInt(lines.get(0));
//                            if (n > 100000) {
//                                player.sendMessage(Utils.applyColor("&cSố Quá Lớn@@"));
//                                player.closeInventory();
//                                return;
//                            }
//                            arena.data.regenRegion = n * 1000;
//                            player.sendMessage(Utils.applyColor("&aThay Đổi Thành Công!"));
//                            arena.regenRegion = System.currentTimeMillis() + (n * 1000L);
//                            new SetupGUI().open(player, arena);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            player.sendMessage(Utils.applyColor("&cKhông Thể Nhận Dạng '" + lines.get(0) + "' Là Số Nào!"));
//                        }
//                    }
//                }.setLine(1, "^^^^^").setLine(2, "Đơn Vị: Giây").setLine(3, "(Chỉ Ghi Số!)").open(player);
            if (slot == 10) {
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
                                    .addLore(PacketReader.farm1.contains(XMaterial.matchXMaterial(material)) ? "&7- Các Loại Cây Trồng Không Cần Độ Cứng!" :
                                            "&7- Độ Cứng Block: &6" + arena.data.miningSpeed.get(material.name()) / 1000 + "s")
                                    .addLore("")
                                    .addLore(PacketReader.farm1.contains(XMaterial.matchXMaterial(material)) ? "" : "&eRight-Click Để Chỉnh Sửa Độ Cứng Của Block Này")
                                    .addLore("&eLeft-Click Để Chỉnh EXP Drop Cho Block")
                                    .build();
                            ct.add(ib);
                        }
                        return ct;
                    }
                }, 54, "Block Edit", new SelectionGUI.SG_OnClick() {
                    @Override
                    public void run(ItemStack clicked, int stt, ClickType clickType, SelectionGUI instance) {
                        if (clickType.equals(ClickType.RIGHT)) {
                            if (PacketReader.farm2.contains(XMaterial.matchXMaterial(clicked.getType()))) return;
                            new SignGUI() {
                                @Override
                                public void onClose(Player player, List<String> lines) {
                                    try {
                                        double speed = Double.parseDouble(lines.get(0));
                                        if (speed > 100000) {
                                            player.sendMessage(Utils.applyColor("&cSố Quá Lớn@@"));
                                            player.closeInventory();
                                            return;
                                        }
                                        double old = arena.data.miningSpeed.get(clicked.getType().name());
                                        arena.data.miningSpeed.replace(clicked.getType().name(), old, (double) (speed * 1000));
                                        instance.openGUI(dgPlayer, instance.page);
                                    } catch (Exception e) {
                                        player.sendMessage(Utils.applyColor("&cKhông Thể Nhận Dạng '" + lines.get(0) + "' Là Số Nào!"));
                                    }
                                }
                            }.setLine(1, "^^^^^").setLine(2, "Nhập Giá Trị").open(player);
                        } else if (clickType.equals(ClickType.LEFT)) {
                            new SignGUI() {
                                @Override
                                public void onClose(Player player, List<String> lines) {
                                    try {
                                        int i = Integer.parseInt(lines.get(0));
                                        if (i > 100000) {
                                            player.sendMessage(Utils.applyColor("&cSố Quá Lớn@@"));
                                            player.closeInventory();
                                            return;
                                        }
                                        int old = arena.data.expDrop.get(SelectionGUI.materialConverterBA(clicked.getType()).name());
                                        arena.data.expDrop.replace(SelectionGUI.materialConverterBA(clicked.getType()).name(), old, i);
                                        instance.openGUI(dgPlayer, instance.page);
                                    } catch (Exception e) {
                                        player.sendMessage(Utils.applyColor("&cKhông Thể Nhận Dạng '" + lines.get(0) + "' Là Số Nào!"));
                                    }
                                }
                            }.setLine(1, "^^^^^").setLine(2, "Nhập Giá Trị").open(player);
                        }
                    }
                }, null, () -> new SetupGUI().open(player, arena)).openGUI(dgPlayer, 1);
            } else if (slot == 11) {
                if (event.getClick().equals(ClickType.LEFT)) {
                    if (arena.data.regenMode.equals(RegenMode.PER_BLOCK)) {
                        arena.data.regenMode = RegenMode.TIME;
                    } else if (arena.data.regenMode.equals(RegenMode.TIME)) {
                        arena.data.regenMode = RegenMode.EMPTY;
                    } else {
                        arena.data.regenMode = RegenMode.PER_BLOCK;
                    }
                    new SetupGUI().open(player, arena);
                } else if (event.getClick().equals(ClickType.RIGHT)) {
                    if (arena.data.regenMode.equals(RegenMode.TIME)) {
                        new SignGUI() {
                            @Override
                            public void onClose(Player player, List<String> lines) {
                                try {
                                    int value = Integer.parseInt(lines.get(0));
                                    if (value > 100000) {
                                        player.sendMessage(Utils.applyColor("&cSố Quá Lớn@@"));
                                        player.closeInventory();
                                        return;
                                    }
                                    arena.data.regenCD_time = (value * 1000);
                                    arena.regenRegion = System.currentTimeMillis();
                                    new SetupGUI().open(player, arena);
                                } catch (Exception e) {
                                    player.sendMessage(Utils.applyColor("&cKhông Thể Nhận Dạng '" + lines.get(0) + "' Là Số Nào!"));
                                }
                            }
                        }.setLine(1, "^^^^^").setLine(2, "Nhập Giá Trị").open(player);
                    }
                }
            } else if (slot == 12) {
//                if (event.getClick().equals(ClickType.LEFT)) {
//                    if (arena.data.enc.equals(EnumType.BLOCK)) arena.data.enc = EnumType.CROP;
//                    else if (arena.data.enc.equals(EnumType.CROP)) arena.data.enc = EnumType.BLOCK;
//                    open(player,arena);
//                } else if (event.getClick().equals(ClickType.RIGHT)) {
                new SignGUI() {
                    @Override
                    public void onClose(Player player, List<String> lines) {
                        try {
                            int level = Integer.parseInt(lines.get(0));
                            if (event.getClick().equals(ClickType.LEFT)) {
                                arena.data.encRequire.replace(EnchantType.MULTIPLE, level);
                            } else if (event.getClick().equals(ClickType.RIGHT)) {
                                arena.data.encRequire.replace(EnchantType.DROP, level);
                            }
                            new SetupGUI().open(player, arena);
                        } catch (Exception e) {
                            player.sendMessage(Utils.applyColor("Không Thể Nhận Diện Số: " + lines.get(0)));
                        }
                    }
                }.setLine(1, "^^^^^").setLine(2, "Nhập Giá Trị").open(player);
//                    new SignGUI() {
//                        @Override
//                        public void onClose(Player player, List<String> lines) {
//                            try {
//                                int value = Integer.parseInt(lines.get(0));
//                                int oldValue = arena.data.encRequire.get(arena.data.enc);
//                                arena.data.encRequire.replace(arena.data.enc,oldValue,value);
//                                SetupGUI.this.open(player, arena);
//                            } catch (Exception e) {
//                                player.sendMessage(Utils.applyColor("&cKhông Thể Nhận Dạng '" + lines.get(0) + "' Là Số Nào!"));
//                            }
//                        }
//                    }.setLine(1,"^^^^^").setLine(2,"Nhập Giá Trị").open(player);
                //}
            } else if (slot == 13) {
                new SignGUI() {
                    @Override
                    public void onClose(Player player, List<String> lines) {
                        try {
                            int value = Integer.parseInt(lines.get(0));
                            if (value > 100000) {
                                player.sendMessage("Số Quá Lớn!");
                                return;
                            }
                            arena.data.levelRequire = value;
                            SetupGUI.this.open(player, arena);
                        } catch (Exception e) {
                            player.sendMessage(Utils.applyColor("&cKhông Thể Nhận Dạng '" + lines.get(0) + "' Là Số Nào!"));
                        }
                    }
                }.setLine(1, "^^^^^").setLine(2, "Nhập Giá trị").open(player);
//                ItemStack plus = new ItemBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIzMzJiNzcwYTQ4NzQ2OTg4NjI4NTVkYTViM2ZlNDdmMTlhYjI5MWRmNzY2YjYwODNiNWY5YTBjM2M2ODQ3ZSJ9fX0=")
//                        .setDisplayName("&aThêm Custom Drop")
//                        .addLore("")
//                        .addLore("&aClick Vào Vật Phẩm Trong Túi Đồ Để Chọn!")
//                        .build();
//                new SelectionGUI(new SelectionGUI.SG_Content() {
//                    @Override
//                    public List<ItemStack> get() {
//                        List<ItemStack> ct = new ArrayList<>();
//                        for (CIDropChance ci : arena.data.ciDropChances) {
//                            ct.add(new ItemBuilder(ItemData.stringToIs(ci.data))
//                                    .addLore("")
//                                    .addLore("&6&m-----------")
//                                    .addLore("&7Drop Chance:&6 " + ci.chance + "%")
//                                    .addLore("")
//                                    .addLore("&eLEFT-CLICK để thay đổi tỉ lệ drop!")
//                                    .addLore("&eRIGHT-CLICK để xóa custom drop này!")
//                                    .build());
//                        }
//                        ct.add(plus);
//                        return ct;
//                    }
//                }, 54, "Custom Item Drop", new SelectionGUI.SG_OnClick() {
//                    @Override
//                    public void run(ItemStack clicked, int stt, ClickType clickType, SelectionGUI instance) {
//                        if (arena.data.ciDropChances.size() - 1 < stt - 9) return;
//                        CIDropChance current = arena.data.ciDropChances.get(stt - 9 + (36 * (instance.page - 1)));
//                        if (current == null)
//                            current = arena.data.ciDropChances.get(arena.data.ciDropChances.size() - 1);
//                        if (ItemBuilder.simpleEqual(plus, clicked)) return;
//                        if (clickType.isLeftClick()) {
//                            CIDropChance finalCurrent = current;
//                            new SignGUI() {
//                                @Override
//                                public void onClose(Player player, List<String> lines) {
//                                    try {
//                                        double n = Double.valueOf(lines.get(0));
//                                        if (n > 100) {
//                                            player.sendMessage(Utils.applyColor("&cSố Đc Nhập Không Được Lớn Hơn 100!"));
//                                            return;
//                                        }
//                                        double old = 0;
//                                        for (CIDropChance ci : arena.data.ciDropChances)
//                                            old += ci.chance;
//                                        if (old + n - finalCurrent.chance > 100) {
//                                            player.sendMessage(Utils.applyColor("&cTổng Các Tỉ Lệ Đang >100!"));
//                                            return;
//                                        }
//                                        finalCurrent.chance = n;
//                                        player.sendMessage(Utils.applyColor("&aThay Đổi Thành Công!"));
//                                        instance.openGUI(dgPlayer, instance.page);
//                                    } catch (Exception e) {
//                                        player.sendMessage(Utils.applyColor("&cKhông Thể Nhận Dạng '" + lines.get(0) + "' Là Số Nào!"));
//                                    }
//                                }
//                            }.setLine(1, "^^^^^").setLine(2, "Nhập Tỉ Lệ Mới!").open(player);
//                        } else if (clickType.isRightClick()) {
//                            arena.data.ciDropChances.remove(current);
//                            instance.openGUI(dgPlayer, instance.page);
//                        }
//                    }
//                }, new SelectionGUI.SG_OnClick() {
//                    @Override
//                    public void run(ItemStack clicked, int stt, ClickType clickType, SelectionGUI instance) {
//                        if (clicked == null || clicked.getType() == XMaterial.AIR.parseMaterial()) return;
//                        arena.data.ciDropChances.add(new CIDropChance(ItemData.isToString(clicked), 0.0));
//                        instance.openGUI(dgPlayer, instance.page);
//                    }
//                }, () -> new SetupGUI().open(player, arena)).openGUI(dgPlayer, 1);
            } else if (slot == 14) {
                new SignGUI() {
                    @Override
                    public void onClose(Player player, List<String> lines) {
                        if (lines.get(0).length() > 0) {
                            arena.data.permission = lines.get(0);
                            player.sendMessage(Utils.applyColor("&aThay Đổi Thành Công!"));
                            new SetupGUI().open(player, arena);
                        } else {
                            player.sendMessage(Utils.applyColor("&cPermission k hợp lệ"));
                        }
                    }
                }.setLine(1, "^^^^^").setLine(2, "Nhập Permission Mới!").open(player);
            } else if (slot == 15) {
                final ItemStack plus = new ItemBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIzMzJiNzcwYTQ4NzQ2OTg4NjI4NTVkYTViM2ZlNDdmMTlhYjI5MWRmNzY2YjYwODNiNWY5YTBjM2M2ODQ3ZSJ9fX0=")
                        .setDisplayName("&aThêm Drop")
                        .addLore("&7- Ấn Để Thêm Vật Chủ")
                        .addLore("")
                        .addLore("&eClick vào để thêm")
                        .build();
                new SelectionGUI(new SelectionGUI.SG_Content() {
                    @Override
                    public List<ItemStack> get() {
                        List<ItemStack> is = new ArrayList<>();
                        for (String k : arena.data.ciBlockDropChance.keySet()) {
                            List<String> lore = new ArrayList<>();
                            for (CIDropChance ci : arena.data.ciBlockDropChance.get(k)) {
                                ItemStack i = ItemData.stringToIs(ci.data).clone();
                                lore.add("&7 -&f " + ItemBuilder.getDisplayName(i) + "&fx" + i.getAmount() + "&6 (" + ci.chance + "%)");
                            }
                            is.add(new ItemBuilder(XMaterial.valueOf((k.toUpperCase())).parseMaterial()).setDisplayName("&f" + k)
                                    .addLore("")
                                    .addLore("&7- Drops:")
                                    .addLore(lore)
                                    .addLore("")
                                    .addLore("&eLEFT-CLICK để chỉnh sửa!")
                                    .addLore("&eRIGHT-CLICK để xóa!")
                                    .build());
                        }
                        is.add(plus);
                        return is;
                    }
                }, 54, "Drop", new SelectionGUI.SG_OnClick() {
                    @Override
                    public void run(ItemStack clicked, int stt, ClickType clickType, SelectionGUI instance) {
                        SelectionGUI sg = instance;
                        if (ItemBuilder.simpleEqual(clicked, plus)) {
                            new SelectionGUI(new SelectionGUI.SG_Content() {
                                @Override
                                public List<ItemStack> get() {
                                    List<ItemStack> list = new ArrayList<>();
                                    List<String> materialsExits = new ArrayList<>();
                                    for (String m : arena.data.region.values()) {
                                        if (materialsExits.contains(m)) continue;
                                        materialsExits.add(m);
                                    }
                                    for (String m : materialsExits) {
                                        list.add(new ItemBuilder(SelectionGUI.materialConverterAB(XMaterial.valueOf((m)).parseMaterial()))
                                                .setDisplayName(m)
                                                .addLore("")
                                                .addLore("&7&m------------")
                                                .addLore("&eClick Để Chọn Block Này!").build());
                                    }
                                    return list;
                                }
                            }, 54, "New Drop", new SelectionGUI.SG_OnClick() {
                                @Override
                                public void run(ItemStack clicked, int stt, ClickType clickType, SelectionGUI instance) {
                                    arena.data.ciBlockDropChance.put(clicked.getType().name(), new ArrayList<>());
                                    sg.openGUI(dgPlayer, instance.page);
                                }
                            }, null, () -> {
                                sg.openGUI(dgPlayer, sg.page);
                            }).openGUI(dgPlayer, 1);
                            return;
                        }
                        String k = clicked.getType().name().replace("§f", "");
                        List<CIDropChance> v = arena.data.ciBlockDropChance.get(k);
                        if (clickType.equals(ClickType.LEFT)) {
                            ItemStack pplus = new ItemBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIzMzJiNzcwYTQ4NzQ2OTg4NjI4NTVkYTViM2ZlNDdmMTlhYjI5MWRmNzY2YjYwODNiNWY5YTBjM2M2ODQ3ZSJ9fX0=")
                                    .setDisplayName("&aThêm Drop")
                                    .addLore("")
                                    .addLore("&eClick Vào Item Trong Túi Đồ")
                                    .build();
                            new SelectionGUI(new SelectionGUI.SG_Content() {
                                @Override
                                public List<ItemStack> get() {
                                    List<ItemStack> rs = new ArrayList<>();
                                    for (CIDropChance ci : v) {
                                        rs.add(new ItemBuilder(ItemData.stringToIs(ci.data).clone())
                                                .addLore("")
                                                .addLore("&7&m---------------")
//                                                .addLore("&7- Tỉ Lệ Đã Chọn: &6" + (ci.default_drop ? "&m" + ci.chance: ci.chance) + "%")
//                                                        .addLore("&7- Default: &d" + ci.default_drop)
                                                .addLore("")
                                                .addLore("&eSHIFT-CLICK để bật/tắt default drop")
                                                .addLore("&eLEFT-CLICK để chỉnh tỉ lệ drop")
                                                .addLore("&eRIGHT-CLICK để xóa")
                                                .build());
                                    }
                                    rs.add(pplus);
                                    return rs;
                                }
                            }, 54, "Drop: " + k, new SelectionGUI.SG_OnClick() {
                                @Override
                                public void run(ItemStack clicked, int stt, ClickType clickType, SelectionGUI instance) {
                                    //      if (arena.data.ciBlockDropChance.size() - 1 < stt - 9) return;
                                    if (ItemBuilder.simpleEqual(clicked, pplus)) return;
                                    int i = (stt - 9) + ((instance.size - 18) * (instance.page - 1));
                                    CIDropChance current = arena.data.ciBlockDropChance.get(k).get(i);
                                    if (clickType.equals(ClickType.LEFT)) {
                                        new SignGUI() {
                                            @Override
                                            public void onClose(Player player, List<String> lines) {
                                                try {
                                                    double i = Integer.parseInt(lines.get(0));
                                                    double j = 0;
                                                    for (CIDropChance c2 : v)
                                                        j += c2.chance;
                                                    if (i + j - current.chance > 100) {
                                                        player.sendMessage(Utils.applyColor("&cTổng Số Tỉ Lệ Đã Lớn Hơn 100!"));
                                                        return;
                                                    }
                                                    current.chance = (double) i;
                                                    player.sendMessage(Utils.applyColor("&aSửa Đổi Thành Công"));
                                                    instance.openGUI(dgPlayer, instance.page);
                                                } catch (Exception e) {
                                                    player.sendMessage(Utils.applyColor("&cSố Không Hợp Lệ: " + lines.get(0)));
                                                }
                                            }
                                        }.setLine(1, "^^^^^").setLine(2, "Nhập Tỉ Lệ").open(player);
                                    } else if (clickType.equals(ClickType.RIGHT)) {
                                        v.remove(i);
                                        instance.openGUI(dgPlayer, instance.page);
                                    } else if (clickType.equals(ClickType.SHIFT_LEFT) || clickType.equals(ClickType.SHIFT_RIGHT)) {
//                                        current.default_drop = !current.default_drop;
                                        instance.openGUI(dgPlayer, instance.page);
                                    }
                                }
                            }, new SelectionGUI.SG_OnClick() {
                                @Override
                                public void run(ItemStack clicked, int stt, ClickType clickType, SelectionGUI instance) {
                                    if (clicked == null || clicked.getType() == XMaterial.AIR.parseMaterial()) return;
                                    v.add(new CIDropChance(ItemData.isToString(clicked), 0.0));
                                    instance.openGUI(dgPlayer, instance.page);
                                }
                            }, () -> {
                                sg.openGUI(dgPlayer, 1);
                            }).openGUI(dgPlayer, 1);
                        } else if (clickType.equals(ClickType.RIGHT)) {
                            arena.data.ciBlockDropChance.remove(k);
                            instance.openGUI(dgPlayer, 1);
                        }
                    }
                }, null, () -> {
                    new SetupGUI().open(player, arena);
                }).openGUI(dgPlayer, 1);
            } else if (slot == 16) {
                new SignGUI() {
                    @Override
                    public void onClose(Player player, List<String> lines) {
                        try {
                            int d = Integer.parseInt(lines.get(0));
                            if (d > 1000) {
                                player.sendMessage(Utils.applyColor("&cSố Quá Lớn!"));
                                return;
                            }
                            arena.data.bbd = d;
                            new SetupGUI().open(player, arena);
                        } catch (Exception e) {
                            player.sendMessage(Utils.applyColor("&cKo Thể Nhận Diện Chữ Số: " + lines.get(0)));
                        }
                    }
                }.setLine(1, "^^^^^").setLine(2, "Nhập Giá Trị").open(player);
            } else if (slot == 17) {
                if (event.getClick().equals(ClickType.LEFT)) {
                    new SignGUI() {
                        @Override
                        public void onClose(Player player, List<String> lines) {
                            String str = lines.get(0);
                            Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(str);
                            if (xMaterial.isPresent()) {
                                arena.data.block_waiting = xMaterial.get();
                                new SetupGUI().open(player, arena);
                            } else {
                                player.sendMessage(Utils.applyColor("&cMaterial Không Hợp Lệ"));
                            }
                        }
                    }.setLine(1, "^^^^").setLine(2, "Nhập Tên Material").open(player);
                } else if (event.getClick().equals(ClickType.RIGHT)) {
                    if (arena.data.block_waiting != null) {
                        arena.data.block_waiting = null;
                    } else {
                        arena.data.block_waiting = XMaterial.BEDROCK;
                    }
                    new SetupGUI().open(player, arena);
                }
            }
        }
    }
}