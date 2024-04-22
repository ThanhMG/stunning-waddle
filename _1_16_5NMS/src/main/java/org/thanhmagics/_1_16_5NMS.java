package org.thanhmagics;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class _1_16_5NMS implements NMSManager {
    @Override
    public Channel getChannel(Player player) {
        return ((NetworkManager) networkManager(player)).channel;
    }

    @Override
    public PPIBlockDig packet(Object packet) {
        return new PPIBlockDig(packet) {
            @Override
            public void init() {
                PacketPlayInBlockDig packetPlayInBlockDig = (PacketPlayInBlockDig) packet;
                this.blockPoss = packetPlayInBlockDig.a();
                this.x = packetPlayInBlockDig.b().getX();
                this.y = packetPlayInBlockDig.b().getY();
                this.z = packetPlayInBlockDig.b().getZ();
                this.packet = packetPlayInBlockDig;
                this.type = packetPlayInBlockDig.d().name();
            }
        };
    }
    @Override
    public void sendBlockBreakAnimation(Player player, int x, int y, int z, int stage) {
        sendPacket(player, new PacketPlayOutBlockBreakAnimation(0,new BlockPosition(x,y,z),stage));
    }
    @Override
    public void openSG(Player player, Object signGUI, Plugin plugin,Material m) {
        Location signLoc = player.getLocation();
        signLoc = signLoc.clone().subtract(0.0, 3.0, 0.0);
        Material material = player.getWorld().getBlockAt(signLoc).getType();
        BlockPosition position = new BlockPosition(signLoc.getBlockX(), signLoc.getBlockY(), signLoc.getBlockZ());
        TileEntitySign tileEntity = new TileEntitySign();
        tileEntity.setPosition(position);
        try {
            List<String> content = (List<String>) signGUI.getClass().getField("lines").get(signGUI);
            for (int i = 0; i < content.size(); i++) {
                tileEntity.a(i, IChatBaseComponent.ChatSerializer.a(ChatColor.translateAlternateColorCodes('&',content.get(i))));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        NetworkManager networkManager = (NetworkManager) networkManager(player);

        player.sendBlockChange(signLoc, m.createBlockData());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(tileEntity.getUpdatePacket());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutOpenSignEditor(position));

        ChannelPipeline pipeline = networkManager.channel.pipeline();
        if (pipeline.names().contains("SignGUI")) {
            pipeline.remove("SignGUI");
        }
        Location finalSignLoc = signLoc;
        pipeline.addAfter("decoder", "SignGUI", new MessageToMessageDecoder<Packet<?>>() {
            @Override
            protected void decode(ChannelHandlerContext chc, Packet<?> packet, List<Object> out) {
                try {
                    if (packet instanceof PacketPlayInUpdateSign) {
                        if (((PacketPlayInUpdateSign) packet).b().equals(position)) {
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                pipeline.remove("SignGUI");
                                player.sendBlockChange(finalSignLoc, finalSignLoc.getBlock().getBlockData());
                                finalSignLoc.getBlock().setType(material);
                                try {
                                    List<String> str = new ArrayList<>();
                                    Collections.addAll(str, ((PacketPlayInUpdateSign) packet).c());
                                    Method onCloseMethod = signGUI.getClass().getDeclaredMethod("onClose", Player.class, List.class);
                                    onCloseMethod.setAccessible(true);
                                    onCloseMethod.invoke(signGUI, player, str);
                                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                out.add(packet);
            }
        });
    }

    @Override
    public org.bukkit.inventory.ItemStack skullWithValue(String value,Material material) {
        org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack(material, 1, (short) 3);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", value));
        Field profileField;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);

            itemStack.setItemMeta(meta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return itemStack;
    }

    @Override
    public Object networkManager(Player player) {
        Field field = null;
        try {
            field = ((CraftPlayer) player).getHandle().playerConnection.getClass().getDeclaredField("networkManager");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        field.setAccessible(true);
        try {
            return field.get(((CraftPlayer) player).getHandle().playerConnection);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendPacket(Player player, Object packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket((Packet<?>) packet);
    }

    @Override
    public NBTStorage nbtStorage() {
        return new NBTStorage() {
            @Override
            public org.bukkit.inventory.ItemStack put(org.bukkit.inventory.ItemStack itemStack, String key, String value) {
                net.minecraft.server.v1_16_R3.ItemStack nms = nmsItemStack(itemStack);
                if (contain(itemStack, key))
                    nms = CraftItemStack.asNMSCopy(remove(itemStack, key));
                NBTTagCompound tag = nms.getOrCreateTag();
                tag.setString(key, value);
                nms.setTag(tag);
                return CraftItemStack.asBukkitCopy(nms);
            }

            @Override
            public boolean contain(org.bukkit.inventory.ItemStack itemStack, String key) {
                return get(itemStack, key) != null;
            }

            @Override
            public String get(org.bukkit.inventory.ItemStack itemStack, String key) {
                net.minecraft.server.v1_16_R3.ItemStack nms = nmsItemStack(itemStack);
                String rs = nms.getOrCreateTag().getString(key);
                if (key.equals("")) return null;
                return rs;
            }

            @Override
            public org.bukkit.inventory.ItemStack remove(org.bukkit.inventory.ItemStack itemStack, String key) {
                if (!contain(itemStack, key)) return itemStack;
                net.minecraft.server.v1_16_R3.ItemStack nms = nmsItemStack(itemStack);
                NBTTagCompound tag = nms.getOrCreateTag();
                tag.remove(key);
                nms.setTag(tag);
                return CraftItemStack.asBukkitCopy(nms);
            }
        };
    }

    net.minecraft.server.v1_16_R3.ItemStack nmsItemStack(org.bukkit.inventory.ItemStack bukkit) {
        return CraftItemStack.asNMSCopy(bukkit);
    }
}