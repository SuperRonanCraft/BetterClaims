package me.RonanCraft.Pueblos.inventory.types;

import me.RonanCraft.Pueblos.inventory.*;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.tools.HelperItem;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryClaimSelect extends PueblosInvLoader implements PueblosInv_MultiClaim {

    private final HashMap<Player, HashMap<Integer, PueblosItem>> itemInfo = new HashMap<>();
    private final HashMap<Player, List<Claim>> claims = new HashMap<>();

    @Override
    public Inventory open(Player p, List<Claim> claims) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, getTitle(p, claims));

        addBorder(inv);

        HashMap<Integer, PueblosItem> itemInfo = new HashMap<>();
        int slot = 0;
        for (Claim claim : claims) {
            slot = getNextSlot(slot, inv);
            if (slot == -1)
                break;
            ItemStack item = getItem(ITEMS.SELECT.section, p, claim);
            //if (claim.contains(p.getLocation()))
            //    HelperItem.enchantItem(item, Enchantment.values()[0]);
            inv.setItem(slot, item);
            itemInfo.put(slot, new PueblosItem(item, ITEM_TYPE.NORMAL, claim));
        }

        this.itemInfo.put(p, itemInfo);
        this.claims.put(p, claims);
        p.openInventory(inv);
        return inv;
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (    !this.itemInfo.containsKey(p)
                || !this.claims.containsKey(p)
                || checkItems(e, itemInfo.get(p)))
            return;

        Claim claim = (Claim) itemInfo.get(p).get(e.getSlot()).info;
        PueblosInventory.CLAIM.open(p, claim, false);
    }

    @Override
    public void clear(Player p) {
        itemInfo.remove(p);
    }

    @Override
    protected String getSection() {
        return "ClaimSelect";
    }

    @Override
    protected List<String> getSections() {
        List<String> list = new ArrayList<>();
        for (ITEMS set : ITEMS.values())
            list.add(set.section);
        return list;
    }

    private enum ITEMS {
        SELECT("Select");

        String section;

        ITEMS(String section) {
            this.section = section;
        }
    }
}
