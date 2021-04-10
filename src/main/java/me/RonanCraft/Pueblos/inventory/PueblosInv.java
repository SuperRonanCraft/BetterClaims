package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.files.msgs.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        meta.setDisplayName(Message.placeholder(p, title, null));
        item.setItemMeta(meta);
    }

    static void setLore(ItemStack item, Player p, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        lore.forEach(str -> lore.set(lore.indexOf(str), Message.placeholder(p, "&f" + str, null)));
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    default void addBorder(Inventory inv) {
        //Decoration
        ItemStack item = Pueblos.getInstance().getSystems().getGlobalItems().getItem(GlobalItems.GLOBAL_ITEM.BORDER, null, null);
        if (item != null)
            for (int i = 0; i < inv.getSize(); i++) {
                if (i < 9 || i > inv.getSize() - 9 || i % 9 == 0 || i % 9 - 8 == 0)
                    inv.setItem(i, item.clone());
            }
    }

    default void addButtonBack(Inventory inv, Player p, HashMap<Integer, PueblosItem> itemInfo, PueblosInventory currentinv, Object info) {
        PueblosInventory pinv = getPl().getSystems().getPlayerInfo().getPrevious(p, currentinv);
        if (pinv != null) {
            int slot = inv.firstEmpty();
            ItemStack item = Pueblos.getInstance().getSystems().getGlobalItems().getItem(GlobalItems.GLOBAL_ITEM.BACK, p, info);
            inv.setItem(slot, item);
            itemInfo.put(slot, new PueblosItem(item, ITEM_TYPE.BACK, pinv, info));
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

    default int getNextSlot(int slot, Inventory inv) {
        slot++;
        while (slot < inv.getSize() && inv.getItem(slot) != null)
            slot++;
        if (slot >= inv.getSize())
            slot = -1;
        return slot;
    }

    void clear(Player p); //Called when a special item causes this inventory to close
}
