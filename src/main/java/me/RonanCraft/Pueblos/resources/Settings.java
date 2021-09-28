package me.RonanCraft.Pueblos.resources;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.files.FileOther;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Settings {

    HashMap<SETTING, Object> settings = new HashMap<>();

    public void load() {
        for (SETTING setting : SETTING.values())
            settings.put(setting, setting.getValue());
    }

    public int getInt(SETTING setting) {
        if (setting.type == TYPE.INT)
            return (Integer) settings.get(setting);
        Pueblos.getInstance().getLogger().severe("Invalid return for setting " + setting.name() + "! Please report this to the Developer!");
        return 0;
    }

    public boolean getBoolean(SETTING setting) {
        if (setting.type == TYPE.BOOLEAN)
            return (Boolean) settings.get(setting);
        Pueblos.getInstance().getLogger().severe("Invalid return for setting " + setting.name() + "! Please report this to the Developer!");
        return false;
    }

    public String getString(SETTING setting) {
        if (setting.type == TYPE.STRING)
            return (String) settings.get(setting);
        Pueblos.getInstance().getLogger().severe("Invalid return for setting " + setting.name() + "! Please report this to the Developer!");
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<String> getStringList(SETTING setting) {
        if (setting.type == TYPE.STRING_LIST) {
            return (List<String>) settings.get(setting);
        }
        Pueblos.getInstance().getLogger().severe("Invalid return for setting " + setting.name() + "! Please report this to the Developer!");
        return null;
    }

    public enum SETTING {
        CLAIM_MAXDEPTH("Claim.MaxDepth", TYPE.INT),
        CLAIM_MAXSIZE("Claim.MaxSize", TYPE.INT),
        CLAIM_ITEM("Claim.Item", TYPE.STRING),
        CLAIM_COUNT("Claim.MaxClaims", TYPE.INT),
        PLAYER_PROTECTDEATHDROP("Player.ProtectDeathDrops", TYPE.BOOLEAN),
        GLOBAL_ENDERMEN_MOVEBLOCKS("Global.EndermenMoveBlocks", TYPE.BOOLEAN),
        GLOBAL_SILVERFISH_BREAKBLOCKS("Global.SilverfishBreakBlocks", TYPE.BOOLEAN),
        GLOBAL_RABBITS_EATCROPS("Global.RabbitsEatCrops", TYPE.BOOLEAN),
        CLAIM_WORLDS_ENABLED("Claim.WorldsEnabled", TYPE.STRING_LIST),
        ;

        private final String path;
        private final TYPE type;

        SETTING(String path, TYPE type) {
            this.path = path;
            this.type = type;
        }

        private Object getValue() {
            FileOther.FILETYPE config = FileOther.FILETYPE.CONFIG;
            switch (type) {
                case INT: return config.getInt(path);
                case BOOLEAN: return config.getBoolean(path);
                case STRING: return config.getString(path);
                case STRING_LIST: return config.getStringList(path);
            }
            return null;
        }
    }

    private enum TYPE {
        INT, STRING, BOOLEAN, STRING_LIST
    }
}
