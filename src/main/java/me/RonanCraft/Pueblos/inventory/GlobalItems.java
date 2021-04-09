package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.files.FileOther;
import me.RonanCraft.Pueblos.resources.tools.ItemHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class GlobalItems {

    private final HashMap<String, ItemStack> items = new HashMap<>();

    public void load() {
        items.clear();
        FileOther.FILETYPE file = FileOther.FILETYPE.MENU;
        String pre = "Global";
        for (GLOBAL_ITEM set : GLOBAL_ITEM.values()) {
            String _pre = pre + "." + set.section + ".";
            ItemStack item = ItemHelper.getItemFromFile(_pre, file);
            this.items.put(set.section, item);
        }
    }

    public ItemStack getItem(GLOBAL_ITEM item, Player p, Object info) {
        return ItemHelper.getItem(items.get(item.section), p, info);
    }

    public enum GLOBAL_ITEM {
        BACK("Back");

        String section;

        GLOBAL_ITEM(String section) {
            this.section = section;
        }
    }
}
