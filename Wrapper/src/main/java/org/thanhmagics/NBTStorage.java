package org.thanhmagics;

import org.bukkit.inventory.ItemStack;

public interface NBTStorage {

    ItemStack put(ItemStack itemStack, String key, String value);

    boolean contain(ItemStack itemStack,String key);

    String get(ItemStack itemStack,String key);

    ItemStack remove(ItemStack itemStack,String key);
}
