package org.thanhmagics.dgcore.gui;

import me.clip.placeholderapi.PlaceholderAPI;
import me.realized.tm.api.TMAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.thanhmagics.DGCore;
import org.thanhmagics.dgcore.DGPlayer;
import org.thanhmagics.dgcore.EnumUpgradeType;
import org.thanhmagics.dgcore.PlayerUpgrade;
import org.thanhmagics.utils.ItemBuilder;
import org.thanhmagics.utils.Utils;
import org.thanhmagics.utils.XMaterial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerUpgradeGUI implements Listener {

    private static Map<UUID, PlayerUpgradeGUI> map = new HashMap<>();


    Map<Integer,EnumUpgradeType> slotType = new HashMap<>();

    public void open(Player player) {
        DGPlayer dgPlayer = DGCore.getInstance().dataSerialize.player.get(player.getUniqueId().toString());
        FileConfiguration config = DGCore.getInstance().upgrade_gui.config;
        UUID uuid = UUID.randomUUID();
        int invSize = Integer.parseInt(config.getString("size"));
        if (invSize % 9 != 0) throw new RuntimeException("size % 9 != 0");
        Inventory inventory = Bukkit.createInventory(null,invSize, Utils.applyColor(config.getString("title")));

        for (String key : config.getConfigurationSection("content.deco-item").getKeys(false)) {
            inventory.setItem(Integer.parseInt(config.getString("content.deco-item." + key + ".slot")),itemFromConfig(dgPlayer,config,"content.deco-item." +key,null,false));
        }

        int slot = Integer.parseInt(config.getString("content.upgrade.multiplier.slot"));
        inventory.setItem(slot,
                itemFromConfig(dgPlayer,config,"content.upgrade.multiplier",EnumUpgradeType.MULTIPLIER,true));
        slotType.put(slot,EnumUpgradeType.MULTIPLIER);
        slot = Integer.parseInt(config.getString("content.upgrade.ci_drop.slot"));
        inventory.setItem(slot,
                itemFromConfig(dgPlayer,config,"content.upgrade.ci_drop",EnumUpgradeType.CI_DROP,true));
        slotType.put(slot,EnumUpgradeType.CI_DROP);
        slot = Integer.parseInt(config.getString("content.upgrade.token.slot"));
        inventory.setItem(slot,
                itemFromConfig(dgPlayer,config,"content.upgrade.token",EnumUpgradeType.TOKEN,true));
        slotType.put(slot,EnumUpgradeType.TOKEN);

        player.openInventory(inventory);
        dgPlayer.inv = uuid;
        map.put(uuid,this);
    }

    private ItemStack itemFromConfig(DGPlayer dgPlayer,FileConfiguration config, String path, EnumUpgradeType type,boolean replace) {
        ItemBuilder itemBuilder = new ItemBuilder(XMaterial.valueOf(config.getString(path + ".material")).parseMaterial());
        PlayerUpgrade playerUpgrade = null;
        if (type != null)
            playerUpgrade = PlayerUpgrade.upgrades.get(type);
        String d = config.getString(path + ".display");
        List<String> l = config.getStringList(path + ".lore");
        itemBuilder.setDisplayName(replace ? playerUpgrade.replacer(dgPlayer,d) : d);
        itemBuilder.setLore(replace ? playerUpgrade.replacer(dgPlayer,l) : l);
        itemBuilder.setAmount(replace ? Integer.parseInt(playerUpgrade.replacer(dgPlayer,(config.getString(path + ".amount")))) : Integer.parseInt(config.getString(path + ".amount")));
        itemBuilder.setGlow(Boolean.getBoolean(config.getString(path + ".glow")));
        return itemBuilder.build();
    }


    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        DGPlayer dgPlayer = DGCore.getInstance().dataSerialize.player.get(player.getUniqueId().toString());
        if (dgPlayer.inv != null) {
            if (!map.containsKey(dgPlayer.inv)) return;
            event.setCancelled(true);
            PlayerUpgradeGUI instance = map.get(dgPlayer.inv);
            if (instance.slotType.containsKey(event.getSlot())) {
                EnumUpgradeType type = instance.slotType.get(event.getSlot());
                PlayerUpgrade upgrade = PlayerUpgrade.upgrades.get(type);
                Integer cost = upgrade.getCost(Integer.sum(dgPlayer.playerUpgrades.get(type),1));
                int i = (int) TMAPI.getTokens(player);
                if (i < cost) {
                    for (String s : DGCore.getInstance().getConfig().getStringList("message.resources_error"))
                        player.sendMessage(Utils.applyColor(PlaceholderAPI.setPlaceholders(player,s).replace("{name}",upgrade.name)));
                    player.closeInventory();
                    return;
                }
                int old = dgPlayer.playerUpgrades.get(type);
                dgPlayer.playerUpgrades.replace(type,old,Integer.sum(old,1));
                TMAPI.setTokens(player,(i - cost));
                for (String s : DGCore.getInstance().getConfig().getStringList("message.upgrade_success")) {
                    player.sendMessage(Utils.applyColor(PlaceholderAPI.setPlaceholders(player,s).replace("{name}",upgrade.name)));
                }
                dgPlayer.reloadMultiplier();
                new PlayerUpgradeGUI().open(player);
            }
        }
    }


}
