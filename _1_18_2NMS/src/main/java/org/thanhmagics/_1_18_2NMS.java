package org.thanhmagics;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor;
import net.minecraft.world.level.block.entity.TileEntitySign;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class _1_18_2NMS implements NMSManager {
    @Override
    public void sendBlockBreakAnimation(Player player, int x, int y, int z, int stage) {
        sendPacket(player, new PacketPlayOutBlockBreakAnimation(0,new BlockPosition(x,y,z),stage));
    }

    @Override
    public Channel getChannel(Player player) {
        return ((NetworkManager) networkManager(player)).m;
    }

    @Override
    public PPIBlockDig packet(Object packet) {
        return new PPIBlockDig(packet) {
            @Override
            public void init() {
                PacketPlayInBlockDig packetPlayInBlockDig = (PacketPlayInBlockDig) packet;
                this.blockPoss = packetPlayInBlockDig.a();
                this.x = packetPlayInBlockDig.b().u();
                this.y = packetPlayInBlockDig.b().v();
                this.z = packetPlayInBlockDig.b().w();
                this.packet = packetPlayInBlockDig;
                this.type = packetPlayInBlockDig.d().name();
            }
        };
    }
    @Override
    public void openSG(Player player, Object signGUI, Plugin plugin,Material m) {
        Location signLoc = player.getLocation();
        signLoc = signLoc.clone().subtract(0.0, 3.0, 0.0);
        Material material = player.getWorld().getBlockAt(signLoc).getType();
        BlockPosition position = new BlockPosition(signLoc.getBlockX(), signLoc.getBlockY(), signLoc.getBlockZ());
        TileEntitySign tileEntity = new TileEntitySign(position, null);
        try {
            List<String> content = (List<String>) signGUI.getClass().getField("lines").get(signGUI);
            for (int i = 0; i < content.size(); i++) {
               // tileEntity.a(i, IChatBaseComponent.a(ChatColor.translateAlternateColorCodes('&',content.get(i))));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        NetworkManager networkManager = (NetworkManager) networkManager(player);

        player.sendBlockChange(signLoc, m.createBlockData());
        ((CraftPlayer) player).getHandle().b.a(tileEntity.c());
        ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutOpenSignEditor(position));

        ChannelPipeline pipeline = networkManager.m.pipeline();
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
    public ItemStack skullWithValue(String value,Material material) {
        ItemStack itemStack = new ItemStack(material, 1, (short) 3);
//        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
//
//        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
//        profile.getProperties().put("textures", new Property("textures", value));
//        Field profileField;
//        try {
//            profileField = meta.getClass().getDeclaredField("profile");
//            profileField.setAccessible(true);
//            profileField.set(meta, profile);
//
//            itemStack.setItemMeta(meta);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
        return itemStack;
    }

    @Override
    public Object networkManager(Player player) {
        Field field = null;
        try {
            field = ((CraftPlayer) player).getHandle().b.getClass().getDeclaredField("a");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        field.setAccessible(true);
        try {
            return field.get(((CraftPlayer)player).getHandle().b);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendPacket(Player player, Object packet) {
        ((CraftPlayer)player).getHandle().b.a((Packet<?>) packet);
    }

    @Override
    public NBTStorage nbtStorage() {
        return new NBTStorage() {
            @Override
            public ItemStack put(ItemStack itemStack, String key, String value) {
                net.minecraft.world.item.ItemStack nms = nmsItemStack(itemStack);
                if (contain(itemStack,key))
                    nms = CraftItemStack.asNMSCopy(remove(itemStack,key));
                NBTTagCompound tag = nms.u();
                tag.a(key, value);
                nms.c(tag);
                return CraftItemStack.asBukkitCopy(nms);
            }

            @Override
            public boolean contain(ItemStack itemStack, String key) {
                return get(itemStack,key) != null;
            }

            @Override
            public String get(ItemStack itemStack, String key) {
                net.minecraft.world.item.ItemStack nms = nmsItemStack(itemStack);
                String rs = nms.u().l(key);
                if (key.equals("")) return null;
                return rs;
            }

            @Override
            public ItemStack remove(ItemStack itemStack, String key) {
                if (!contain(itemStack,key)) return itemStack;
                net.minecraft.world.item.ItemStack nms = nmsItemStack(itemStack);
                NBTTagCompound tag = nms.u();
                tag.r(key);
                nms.c(tag);
                return CraftItemStack.asBukkitCopy(nms);
            }
        };
    }

    net.minecraft.world.item.ItemStack nmsItemStack(ItemStack bukkit) {
        return CraftItemStack.asNMSCopy(bukkit);
    }
}
