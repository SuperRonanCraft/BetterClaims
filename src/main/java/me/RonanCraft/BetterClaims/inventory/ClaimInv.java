package me.RonanCraft.BetterClaims.inventory;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.resources.helper.HelperPlayer;
import me.RonanCraft.BetterClaims.resources.messages.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ClaimInv {

    default BetterClaims getPl() {
        return BetterClaims.getInstance();
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
        ItemStack item = BetterClaims.getInstance().getGlobalItems().getItem(GlobalItems.GLOBAL_ITEM.BORDER, null, null);
        if (item != null)
            for (int i = 0; i < inv.getSize(); i++) {
                if (i < 9 || i > inv.getSize() - 9 || i % 9 == 0 || i % 9 - 8 == 0)
                    inv.setItem(i, item.clone());
            }
    }

    default void addButtonBack(Inventory inv, Player p, HashMap<Integer, ClaimItem> itemInfo, ClaimInventory currentinv, Object info) {
        ClaimInventory pinv = HelperPlayer.getData(p).getPrevious(currentinv);
        if (pinv != null) {
            int slot = inv.firstEmpty();
            ItemStack item = BetterClaims.getInstance().getGlobalItems().getItem(GlobalItems.GLOBAL_ITEM.BACK, p, info);
            inv.setItem(slot, item);
            itemInfo.put(slot, new ClaimItem(item, ITEM_TYPE.BACK, pinv, info));
        }
    }

    default boolean checkItems(InventoryClickEvent e, HashMap<Integer, ClaimItem> items) {
        return checkItems(e.getSlot(), (Player) e.getWhoClicked(), items);
    }

    default boolean checkItems(int slot, Player p, HashMap<Integer, ClaimItem> items) {
        if (items.containsKey(slot)) {
            ClaimItem item = items.get(slot);
            if (item.type != ITEM_TYPE.NORMAL) {
                switch (item.type) {
                    case BACK:
                    case NEXT:
                        ClaimInventory inv = (ClaimInventory) item.info;
                        HelperPlayer.getData(p).removePrevious();
                        inv.openCasted(p, item.info2);
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

    default void goBack(Player p, HashMap<Integer, ClaimItem> itemInfo) {
        for (Map.Entry<Integer, ClaimItem> info : itemInfo.entrySet()) {
            int slot = info.getKey();
            ClaimItem item = info.getValue();
            if (item.type == ITEM_TYPE.BACK)
                checkItems(slot, p, itemInfo);
        }
    }
}
