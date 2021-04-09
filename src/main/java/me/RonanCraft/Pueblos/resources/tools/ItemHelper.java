package me.RonanCraft.Pueblos.resources.tools;

import me.RonanCraft.Pueblos.inventory.PueblosInv;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import me.RonanCraft.Pueblos.resources.files.FileOther;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class ItemHelper {

    public static ItemStack getItem(ItemStack item, Player p, Object info) {
        //Item Title
        String name = Objects.requireNonNull(item.getItemMeta()).getDisplayName();
        name = PueblosPlaceholders.getPlaceholder(name, p, info);
        PueblosInv.setTitle(item, p, name);
        //Item Lore
        List<String> lore = Objects.requireNonNull(item.getItemMeta().getLore());
        lore.forEach(str -> lore.set(lore.indexOf(str), PueblosPlaceholders.getPlaceholder(str, p, info)));
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

    public static ItemStack getItemFromFile(String path, FileOther.FILETYPE file) {
        //System.out.println(path);
        //Item
        ItemStack item = new ItemStack(getMat(file.getString(path + "Item")));
        //Name
        PueblosInv.setTitle(item, null, file.getString(path + "Name"));
        //Lore
        PueblosInv.setLore(item, null, file.getStringList(path + "Lore"));
        return item;
    }

    private static Material getMat(String str) {
        try {
            return Material.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Material.BEDROCK;
        }
    }
}
