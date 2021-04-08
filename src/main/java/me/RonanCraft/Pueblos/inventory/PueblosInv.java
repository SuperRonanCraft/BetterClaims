package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
}
