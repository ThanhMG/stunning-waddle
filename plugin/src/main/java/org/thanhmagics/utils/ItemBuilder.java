package org.thanhmagics.utils;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.thanhmagics.DGCore;

import java.io.Serializable;
import java.util.*;

public class ItemBuilder implements Serializable {
    private ItemStack
            stack;
    private List<String> lore = new LinkedList<>();

    public ItemBuilder(String skullvalue) {
        this.stack = skullWithValue(skullvalue);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.stack = itemStack;
    }

    public ItemBuilder(Material material) {
        if (material != null) this.stack = new ItemStack(material);
        else stack = new ItemStack(Material.BARRIER);
    }

    public Material getMaterial() {
        return stack.getType();
    }

    public ItemMeta getItemMeta() {
        return stack.getItemMeta();
    }

    public ItemBuilder setColor(Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
        meta.setColor(color);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setGlow(boolean glow) {
        if (glow) {
            addEnchant(Enchantment.KNOCKBACK, 1);
            addItemFlag(ItemFlag.HIDE_ENCHANTS);
        } else {
            ItemMeta meta = getItemMeta();
            for (Enchantment enchantment : meta.getEnchants().keySet()) {
                meta.removeEnchant(enchantment);
            }
        }
        return this;
    }

    public static String getDisplayName(ItemStack itemStack) {
        String rs = null;
        if (itemStack.getItemMeta() != null)
            rs = itemStack.getItemMeta().getDisplayName();
        if (rs == null || rs.length() == 0)
            rs = itemStack.getType().name();
        return rs;
    }

    public static boolean simpleEqual(ItemStack origin, ItemStack other) {
        if (!origin.getType().equals(other.getType())) return false;
        if (origin.getAmount() != origin.getAmount()) return false;
        if (origin.getEnchantments() != other.getEnchantments()) return false;
        if (!origin.getItemMeta().getDisplayName().equals(other.getItemMeta().getDisplayName())) return false;
        if (!origin.getItemMeta().getLore().equals(other.getItemMeta().getLore())) return false;
        return true;
    }

    public ItemBuilder setGlow(Enchantment enchantment) {
        addEnchant(enchantment, 1);
        addItemFlag(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = stack.getItemMeta();
        meta.setUnbreakable(unbreakable);
        stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setBannerColor(DyeColor color) {
        BannerMeta meta = (BannerMeta) stack.getItemMeta();
        meta.setBaseColor(color);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public ItemBuilder setItemMeta(ItemMeta meta) {
        stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setHead(String owner) {
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner(owner);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addLore(String string) {
        ItemMeta meta = getItemMeta();
        this.lore.add(ChatColor.translateAlternateColorCodes('&', string));
        meta.setLore(this.lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addLore(List<String> lore) {
        for (String ss : lore)
            addLore(ss);
        return this;
    }

    public ItemBuilder setDisplayName(String displayname) {
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayname));
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setItemStack(ItemStack stack) {
        this.stack = stack;
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = getItemMeta();
        meta.setLore(color(lore));
        setItemMeta(meta);
        return this;
    }

    private List<String> color(List<String> input) {
        List<String> rs = new ArrayList<>();
        for (String s : input) {
            rs.add(Utils.applyColor(s));
        }
        return rs;
    }

    public ItemBuilder setLore(String lore) {
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(lore);
        ItemMeta meta = getItemMeta();
        meta.setLore(loreList);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        ItemMeta meta = getItemMeta();
        meta.addEnchant(enchantment, level, true);
        setItemMeta(meta);
        return this;
    }

    public static Map<Enchantment, Integer> enchantmentIntegerMap = new HashMap<>();

    public ItemBuilder setEnchant() {
        if (!enchantmentIntegerMap.isEmpty()) {
            for (Map.Entry<Enchantment, Integer> enchantmentIntegerEntry : enchantmentIntegerMap.entrySet())
                getItemMeta().addEnchant(
                        enchantmentIntegerEntry.getKey(),
                        enchantmentIntegerEntry.getValue(),
                        true
                );
        }
        return this;
    }


    public ItemBuilder addItemFlag(ItemFlag flag) {
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(flag);
        setItemMeta(meta);
        return this;
    }


    public ItemStack skullWithValue(String value) {
        return DGCore.getInstance().nmsManager.skullWithValue(value, XMaterial.PLAYER_HEAD.parseMaterial());
    }

    public ItemStack build() {
        return stack;
    }
}