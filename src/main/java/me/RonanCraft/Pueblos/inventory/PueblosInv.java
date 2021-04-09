package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.files.FileOther;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface PueblosInv {

    default Pueblos getPl() {
        return Pueblos.getInstance();
    }

    void clickEvent(InventoryClickEvent e);

    static void setTitle(ItemStack item, Player p, String title) {
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Messages.core.color(p, title));
        item.setItemMeta(meta);
    }

    static void setLore(ItemStack item, Player p, List<String> lore) {
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

    default boolean checkItems(InventoryClickEvent e, HashMap<Integer, PueblosItem> items) {
        if (items.containsKey(e.getSlot())) {
            Player p = (Player) e.getWhoClicked();
            PueblosItem item = items.get(e.getSlot());
            if (item.type != ITEM_TYPE.NORMAL) {
                switch (item.type) {
                    case BACK:
                    case NEXT:
                        PueblosInventory inv = (PueblosInventory) item.info;
                        Pueblos.getInstance().getSystems().getPlayerInfo().removePrevious(p);
                        inv.openCasted((Player) e.getWhoClicked(), item.info2);
                }
                clear(p);
                return true;
            }
        }
        return false;
    }

    void clear(Player p); //Called when a special item causes this inventory to close
}
