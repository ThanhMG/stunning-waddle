package org.thanhmagics;

import io.netty.channel.Channel;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public interface NMSManager {

    Channel getChannel(Player player);

    PPIBlockDig packet(Object packet);

    void openSG(Player player, Object signGUI, Plugin plugin, Material material);

    ItemStack skullWithValue(String value,Material material);

    Object networkManager(Player player);

    void sendPacket(Player player, Object packet);

    void sendBlockBreakAnimation(Player player,int x,int y, int z,int stage);

    NBTStorage nbtStorage();

}
