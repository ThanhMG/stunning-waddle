package org.thanhmagics.dgcore;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.thanhmagics.DGCore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileConfig {

    public String fileName;

    public File file;

    public FileConfiguration config;

    public FileConfig(String fileName) {
        this.fileName = fileName;
    }

    public static List<FileConfig> list = new ArrayList<>();

    public FileConfig init() {
        this.file = new File(DGCore.getInstance().getDataFolder(), fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                DGCore.getInstance().saveResource(fileName,true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        list.add(this);
        this.config = YamlConfiguration.loadConfiguration(file);
        return this;
    }

    public static void reload() {
        for (FileConfig file : list) {
            file.init();
        }
        DGCore.getInstance().reloadConfig();
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
