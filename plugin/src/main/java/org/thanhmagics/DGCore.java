package org.thanhmagics;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.thanhmagics.dgcore.*;
import org.thanhmagics.dgcore.commands.AdminCommand;
import org.thanhmagics.dgcore.commands.PVPCommand;
import org.thanhmagics.dgcore.commands.tab.ADCMTab;
import org.thanhmagics.dgcore.commands.tab.PVPCMTab;
import org.thanhmagics.dgcore.gui.PlayerUpgradeGUI;
import org.thanhmagics.dgcore.gui.SelectionGUI;
import org.thanhmagics.dgcore.gui.SetupGUI;
import org.thanhmagics.dgcore.gui.SetupGUI2;
import org.thanhmagics.dgcore.listener.ChatListener;
import org.thanhmagics.dgcore.listener.Listeners;
import org.thanhmagics.dgcore.listener.PacketReader;
import org.thanhmagics.dgcore.listener.SelectPosition;
import org.thanhmagics.dgcore.commands.DefaultCommand;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class DGCore extends JavaPlugin {

    private static DGCore instance;

    public DataSerialize dataSerialize;

    public NMSManager nmsManager;

    public Map<String, Arena> arenas = new HashMap<>();


    public FileConfig upgrade_file,upgrade_gui,block_name_file;

    @Override
    public void onEnable() {
        // Plugin startup logic
        if (!new File(getDataFolder(), "config.yml").exists()) saveResource("config.yml", true);
        instance = this;
        String version = getServer().getVersion();
        version = version.split("\\(")[1].split("\\)")[0].split("MC: ")[1];
        switch (version) {
            case "1.20.1" -> {nmsManager = new _1_20_1NMS();
            }
            case "1.20.2" -> {nmsManager = new _1_20_2NMS();
            }
            case "1.19.4" -> {nmsManager = new _1_19_4NMS();
            }
            case "1.18.2" -> {nmsManager = new _1_18_2NMS();
            }
            case "1.17.1" -> {nmsManager = new _1_17_1NMS();
            }
            case "1.16.5" -> {nmsManager = new _1_16_5NMS();
            }
        }
        if (nmsManager == null) {
            getServer().getPluginManager().disablePlugin(instance);
            throw new RuntimeException("Khong Ho Tro Phien Ban: " +  version);
        }

        System.out.println("-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-");
        System.out.println("Name: DGCore");
        System.out.println("Author: ThanhMagics (https://facebook.com/ThanhMagics)");
        System.out.println("Version: 1.0");
        System.out.println("NMS: " + version);
        System.out.println("-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-");
        dataSerialize = new DataSerialize();
        if (new File(getDataFolder(), "data-1.1.save").exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(new File(getDataFolder(), "data-1.1.save"));
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                dataSerialize = (DataSerialize) objectInputStream.readObject();
                for (String id : dataSerialize.arena.keySet())
                    new Arena(dataSerialize.arena.get(id));
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        upgrade_file = (new FileConfig("upgrade.yml")).init();
        upgrade_gui = new FileConfig("upgrade_gui.yml").init();
    //    efficiency_file = new FileConfig("efficiency.yml").init();
        block_name_file = new FileConfig("block_name.yml").init();
        PacketReader packetReader = new PacketReader();
        getServer().getPluginManager().registerEvents(new SelectionGUI(null, 9, null, null, null), this);
        getServer().getPluginManager().registerEvents(new SetupGUI(), this);
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        getServer().getPluginManager().registerEvents(new SelectPosition(), this);
        getServer().getPluginManager().registerEvents(packetReader, this);
        getServer().getPluginManager().registerEvents(new PlayerUpgradeGUI(),this);
        getServer().getPluginManager().registerEvents(new ChatListener(),this);
        getServer().getPluginManager().registerEvents(new SetupGUI2(),this);
        getCommand("core").setExecutor(new AdminCommand());
        getCommand("core").setTabCompleter(new ADCMTab());
        getCommand("upgrade").setExecutor(new DefaultCommand());
        getCommand("pvp").setExecutor(new PVPCommand());
        getCommand("pvp").setTabCompleter(new PVPCMTab());
        new PlayerUpgrade("multiplier",upgrade_file.config, EnumUpgradeType.MULTIPLIER).init();
        new PlayerUpgrade("ci_drop",upgrade_file.config, EnumUpgradeType.CI_DROP).init();
        new PlayerUpgrade("token",upgrade_file.config, EnumUpgradeType.TOKEN).init();
        Arena.asyncBlockTrack();
        Listeners.init();
        Multiplier.asyncChecker();
        Listeners.thread();
        Arena.topUpdater();
        Arena.bossBarUpdater();
        PlayerLevel.init(new FileConfig("level.yml").init().config);
        new PAPIHook().register();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!DGCore.getInstance().dataSerialize.player.containsKey(player.getUniqueId().toString())) {
                DGPlayer newplayer = new DGPlayer(player.getUniqueId());
                for (Arena arena : DGCore.getInstance().arenas.values()) {
                    newplayer.mined_in_region.put(arena.id,0);
                }
                DGCore.getInstance().dataSerialize.player.put(newplayer.uuid.toString(), newplayer);
                newplayer.reloadMultiplier();
            } else {
                DGCore.getInstance().dataSerialize.player.get(player.getUniqueId().toString()).reloadMultiplier();
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(getDataFolder(), "data-1.1.save"));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(dataSerialize);
            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        for (BossBar bossBar : Arena.bossBar.values())
            bossBar.removeAll();
//        for (Player player : Bukkit.getOnlinePlayers())
//            player.kickPlayer("Server Reload!");
    }



    public static DGCore getInstance() {
        return instance;
    }
}
