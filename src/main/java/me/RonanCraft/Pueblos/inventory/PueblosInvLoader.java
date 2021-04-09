package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.CLAIM_FLAG_MEMBER;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import me.RonanCraft.Pueblos.resources.files.FileOther;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import me.RonanCraft.Pueblos.resources.tools.ItemHelper;
import me.RonanCraft.Pueblos.resources.tools.PueblosPlaceholders;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class PueblosInvLoader {

    String title = null;
    HashMap<String, ItemStack> items = new HashMap<>();

    ItemStack getItem(String section, Player p, Object info) {
        return ItemHelper.getItem(items.get(section).clone(), p, info);
    }

    String getTitle(Player p, Object info) {
        return PueblosPlaceholders.getPlaceholder(title, p, info);
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
                ItemStack item = ItemHelper.getItemFromFile(_pre, file);
                this.items.put(sec, item);
            }
    }

    abstract String getSection();

    abstract List<String> getSections();

}
