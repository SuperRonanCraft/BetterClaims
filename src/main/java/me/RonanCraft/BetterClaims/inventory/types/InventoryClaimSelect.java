package me.RonanCraft.BetterClaims.inventory.types;

import me.RonanCraft.BetterClaims.inventory.*;
import me.RonanCraft.BetterClaims.claims.ClaimData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryClaimSelect extends PueblosInvLoader implements PueblosInv_MultiClaim {

    private final HashMap<Player, HashMap<Integer, PueblosItem>> itemInfo = new HashMap<>();
    private final HashMap<Player, List<ClaimData>> claims = new HashMap<>();

    @Override
    public Inventory open(Player p, List<ClaimData> claimData) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, getTitle(p, claimData));

        addBorder(inv);

        HashMap<Integer, PueblosItem> itemInfo = new HashMap<>();
        int slot = 0;
        for (ClaimData claim : claimData) {
            slot = getNextSlot(slot, inv);
            if (slot == -1)
                break;
            ITEMS _i = claim.isOwner(p) ? ITEMS.OWNER : ITEMS.MEMBER;
            ItemStack item = getItem(_i.section, p, claim);
            //if (claim.contains(p.getLocation()))
            //    HelperItem.enchantItem(item, Enchantment.values()[0]);
            inv.setItem(slot, item);
            itemInfo.put(slot, new PueblosItem(item, ITEM_TYPE.NORMAL, claim));
        }

        this.itemInfo.put(p, itemInfo);
        this.claims.put(p, claimData);
        p.openInventory(inv);
        return inv;
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (    !this.itemInfo.containsKey(p)
                || !this.claims.containsKey(p)
                || checkItems(e, itemInfo.get(p))
                || !itemInfo.get(p).containsKey(e.getSlot()))
            return;

        ClaimData claimData = (ClaimData) itemInfo.get(p).get(e.getSlot()).info;
        PueblosInventory.CLAIM.open(p, claimData, false);
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
        OWNER("Owner"),
        MEMBER("Member");

        String section;

        ITEMS(String section) {
            this.section = section;
        }
    }
}
