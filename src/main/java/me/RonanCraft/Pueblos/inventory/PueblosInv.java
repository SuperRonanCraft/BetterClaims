package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public interface PueblosInv {

    default Pueblos getPl() {
        return Pueblos.getInstance();
    }

    void clickEvent(InventoryClickEvent e);

    default void setTitle(ItemStack item, Player p, String title) {
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Messages.core.color(p, title));
        item.setItemMeta(meta);
    }

    default void setLore(ItemStack item, Player p, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        lore.forEach(str -> lore.set(lore.indexOf(str), Messages.core.color(p, "&f" + str)));
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    default void addBorder(Inventory inv) {
        //Decoration
        ItemStack border = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        setTitle(border, null, " ");
        for (int i = 0; i < inv.getSize(); i++) {
            if (i < 9 || i > inv.getSize() - 9 || i % 9 == 0 || i % 9 - 8 == 0)
                inv.setItem(i, border.clone());
        }
    }
}
