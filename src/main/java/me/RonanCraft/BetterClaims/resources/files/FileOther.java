package me.RonanCraft.BetterClaims.resources.files;

import me.RonanCraft.BetterClaims.BetterClaims;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileOther {

    List<FILETYPE> types = new ArrayList<>();

    void load() {
        types.clear();
        for (FILETYPE type : FILETYPE.values()) {
            type.load();
            types.add(type);
        }
    }

    public enum FILETYPE {
        CONFIG("config"), MENU("menus"), MYSQL("mysql");

        private final String fileName;
        private final YamlConfiguration config = new YamlConfiguration();
        private final File file;

        FILETYPE(String str) {
            this.fileName = str + ".yml";
            this.file = new File(BetterClaims.getInstance().getDataFolder(), fileName);
        }

        //PUBLIC
        public String getString(String path) {
            if (config.isString(path))
                return config.getString(path);
            return "SOMETHING WENT WRONG";
        }

        public boolean getBoolean(String path) {
            return config.getBoolean(path);
        }

        public int getInt(String path) {
            return config.getInt(path);
        }

        public List<String> getStringList(String path) {
            if (config.isList(path))
                return config.getStringList(path);
            return new ArrayList<>();
        }

        public ConfigurationSection getConfigurationSection(String path) {
            return config.getConfigurationSection(path);
        }

        public boolean isString(String path) {
            return config.isString(path);
        }

        public boolean isList(String path) {
            return config.isList(path);
        }

        public List<Map<?, ?>> getMapList(String path) {
            return config.getMapList(path);
        }

        public YamlConfiguration getConfig() {
            return config;
        }

        public File getFile() {
            return file;
        }

        public void setValue(String path, Object value) {
            config.set(path, value);
        }

        public void reload() {
            load();
        }

        //PROCCESSING
        private void load() {
            if (!file.exists()) {
                BetterClaims.getInstance().saveResource(fileName, false);
                try {
                    config.load(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    config.load(file);
                    final InputStream in = BetterClaims.getInstance().getResource(fileName);
                    if (in != null) {
                        config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(in)));
                        config.options().copyDefaults(true);
                        in.close();
                    }
                    config.save(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
