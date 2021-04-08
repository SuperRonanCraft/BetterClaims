package me.RonanCraft.Pueblos.inventory;

import org.bukkit.inventory.ItemStack;

public class PueblosItem {
    final ItemStack item;
    final ITEM_TYPE type;
    final Object info;
    final Object info2;

    PueblosItem(ItemStack item, ITEM_TYPE type, Object info) {
        this.item = item;
        this.type = type;
        this.info = info;
        this.info2 = null;
    }

    PueblosItem(ItemStack item, ITEM_TYPE type, Object info, Object info2) {
        this.item = item;
        this.type = type;
        this.info = info;
        this.info2 = info2;
    }
}
