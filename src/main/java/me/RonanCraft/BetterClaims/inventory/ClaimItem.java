package me.RonanCraft.BetterClaims.inventory;

import org.bukkit.inventory.ItemStack;

public class ClaimItem {
    protected final ItemStack item;
    protected final ITEM_TYPE type;
    public final Object info;
    public final Object info2;

    public ClaimItem(ItemStack item, ITEM_TYPE type, Object info) {
        this.item = item;
        this.type = type;
        this.info = info;
        this.info2 = null;
    }

    public ClaimItem(ItemStack item, ITEM_TYPE type, Object info, Object info2) {
        this.item = item;
        this.type = type;
        this.info = info;
        this.info2 = info2;
    }
}
