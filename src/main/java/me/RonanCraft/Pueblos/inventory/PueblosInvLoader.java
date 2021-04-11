package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.files.FileOther;
import me.RonanCraft.Pueblos.resources.files.msgs.Message;
import me.RonanCraft.Pueblos.resources.tools.HelperItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class PueblosInvLoader {

    String title = null;
    HashMap<String, ItemStack> items = new HashMap<>();

    ItemStack getItem(String section, Player p, Object info) {
        return HelperItem.getItem(items.get(section).clone(), p, info);
    }

    String getTitle(Player p, Object info) {
        return Message.placeholder(p , title, info);
    }

    public void load() {
        items.clear();
        FileOther.FILETYPE file = FileOther.FILETYPE.MENU;
        String pre = getSection();
        title = file.getString(pre + ".Title");
        List<String> sections = getSections();
        if (sections != null)
            for (String sec : sections) {
                String _pre = pre + ".Items." + sec + ".";
                ItemStack item = HelperItem.getItemFromFile(_pre, file);
                this.items.put(sec, item);
            }
    }

    abstract String getSection();

    abstract List<String> getSections();

}
