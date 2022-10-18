package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.files.FileOther;
import me.RonanCraft.Pueblos.resources.helper.HelperItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class GlobalItems {

    private final HashMap<String, ItemStack> items = new HashMap<>();
    private final HashMap<GLOBAL_ITEM, Boolean> enabled = new HashMap<>();

    public void load() {
        items.clear();
        enabled.clear();
        FileOther.FILETYPE file = FileOther.FILETYPE.MENU;
        String pre = "Global";
        for (GLOBAL_ITEM set : GLOBAL_ITEM.values()) {
            String _pre = pre + "." + set.section + ".";
            ItemStack item = HelperItem.getItemFromFile(_pre, file);
            this.items.put(set.section, item);
            if (set.canDisable)
                this.enabled.put(set, file.getBoolean(_pre + "Enabled"));
        }
    }

    public ItemStack getItem(GLOBAL_ITEM item, Player p, Object info) {
        if (!enabled.containsKey(item) || enabled.get(item))
            return HelperItem.getItem(items.get(item.section), p, info);
        return null;
    }

    public enum GLOBAL_ITEM {
        BACK("Back"),
        BORDER("Border", true);

        String section;
        boolean canDisable;

        GLOBAL_ITEM(String section) {
            this(section, false);
        }

        GLOBAL_ITEM(String section, boolean canDisable) {
            this.section = section;
            this.canDisable = canDisable;
        }
    }
}
