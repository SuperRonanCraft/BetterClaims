package me.RonanCraft.Pueblos.inventory;

import org.bukkit.inventory.ItemStack;

public class PueblosItem {
    final ItemStack item;
    final Object type_info;

    PueblosItem(ItemStack item, Object type_info) {
        this.item = item;
        this.type_info = type_info;
    }


}
