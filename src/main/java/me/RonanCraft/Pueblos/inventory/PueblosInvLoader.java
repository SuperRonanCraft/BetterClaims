package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.CLAIM_FLAG_MEMBER;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import me.RonanCraft.Pueblos.resources.files.FileOther;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
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
        return getItem(items.get(section).clone(), p, info);
    }

    ItemStack getItem(ItemStack item, Player p, Object info) {
        //Item Title
        String name = Objects.requireNonNull(item.getItemMeta()).getDisplayName();
        name = getPlaceholder(name, p, info);
        PueblosInv.setTitle(item, p, name);
        //Item Lore
        List<String> lore = Objects.requireNonNull(item.getItemMeta().getLore());
        lore.forEach(str -> lore.set(lore.indexOf(str), getPlaceholder(str, p, info)));
        PueblosInv.setLore(item, p, lore);
        if (item.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta =(SkullMeta) item.getItemMeta();
            if (info instanceof ClaimMember)
                meta.setOwningPlayer(((ClaimMember) info).getPlayer());
            else if (info instanceof Claim)
                meta.setOwningPlayer(((Claim) info).getOwner());
            else
                meta.setOwningPlayer(Bukkit.getOfflinePlayers()[ThreadLocalRandom.current().nextInt(0, Bukkit.getOfflinePlayers().length)]);
            item.setItemMeta(meta);
        }

        return item;
    }

    private String getPlaceholder(String str, Player p, Object info) {
        if (info == null)
            return str;
        if (info instanceof Claim)
            str = getPlaceholder(str, (Claim) info);
        else if (info instanceof ClaimMember)
            str = getPlaceholder(str, (ClaimMember) info, null);
        else if (info instanceof Object[] && ((Object[]) info).length == 2)
            str = getPlaceholder(str, (Object[]) info);
        return Messages.core.color(p, str);
    }

    private String getPlaceholder(String str, Object[] info) {
        if (info[0] instanceof ClaimMember && info[1] instanceof CLAIM_FLAG_MEMBER)
            str = getPlaceholder(str, (ClaimMember) info[0], (CLAIM_FLAG_MEMBER) info[1]);
        return str;
    }

    private String getPlaceholder(String str, Claim info) {
        if (str.contains("%claim_name%"))
            str = str.replace("%claim_name%", info.getName());
        if (str.contains("%claim_members%"))
            str = str.replace("%claim_members%", String.valueOf(info.getMembers().size()));
        if (str.contains("%claim_owner%"))
            str = str.replace("%claim_owner%", String.valueOf(info.ownerName));
        return str;
    }

    private String getPlaceholder(String str, ClaimMember info, CLAIM_FLAG_MEMBER flag) {
        if (str.contains("%member_name%"))
            str = str.replace("%member_name%", info.getName());
        if (flag != null) {
            if (str.contains("%member_flag%"))
                str = str.replace("%member_flag%", StringUtils.capitalize(flag.name().toLowerCase().replace("_", " ")));
            if (str.contains("%member_flag_value%"))
                str = str.replace("%member_flag_value%", info.getFlags().getOrDefault(flag, flag.getDefault()).toString());
        }
        return str;
    }

    String getTitle(Player p, Object info) {
        return getPlaceholder(title, p, info);
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
                ItemStack item = getItemFromFile(_pre, file);
                this.items.put(sec, item);
            }
    }

    ItemStack getItemFromFile(String path, FileOther.FILETYPE file) {
        //System.out.println(path);
        //Item
        ItemStack item = new ItemStack(getMat(file.getString(path + "Item")));
        //Name
        PueblosInv.setTitle(item, null, file.getString(path + "Name"));
        //Lore
        PueblosInv.setLore(item, null, file.getStringList(path + "Lore"));
        return item;
    }

    Material getMat(String str) {
        try {
            return Material.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Material.BEDROCK;
        }
    }

    abstract String getSection();

    abstract List<String> getSections();

}
