package me.RonanCraft.BetterClaims.inventory;

import me.RonanCraft.BetterClaims.resources.files.FileOther;
import me.RonanCraft.BetterClaims.resources.messages.Message;
import me.RonanCraft.BetterClaims.resources.helper.HelperItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class PueblosInvLoader {

    String title = null;
    HashMap<String, ItemStack> items = new HashMap<>();

    protected ItemStack getItem(String section, Player p, Object info) {
        return HelperItem.getItem(items.get(section).clone(), p, info);
    }

    protected String getTitle(Player p, Object info) {
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

    protected abstract String getSection();

    protected abstract List<String> getSections();

}
