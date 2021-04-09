package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.claims.CLAIM_FLAG;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimRequest;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class InventoryClaimFlags extends PueblosInvLoader implements PueblosInv_Claim {

    private final HashMap<Player, HashMap<Integer, PueblosItem>> itemInfo = new HashMap<>();
    private final HashMap<Player, Claim> claim = new HashMap<>();

    @Override
    public Inventory open(Player p, Claim claim) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, getTitle(p, claim));

        addBorder(inv);

        HashMap<Integer, PueblosItem> itemInfo = new HashMap<>();
        for (CLAIM_FLAG flag : CLAIM_FLAG.values()) {
            ItemStack item;
            if (!claim.hasRequestFrom(p))
                item = getItem(ITEMS.NEW.section, p, claim);
            else
                item = getItem(ITEMS.REQUESTED.section, p, claim);
            int slot = inv.firstEmpty();
            inv.setItem(slot, item);
            itemInfo.put(slot, new PueblosItem(item, ITEM_TYPE.NORMAL, claim));
        }
        this.itemInfo.put(p, itemInfo);
        this.claim.put(p, claim);
        p.openInventory(inv);
        return inv;
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!this.itemInfo.containsKey(p) || !this.claim.containsKey(p) || checkItems(e, itemInfo.get(p)))
            return;

        CLAIM_FLAG flag = (CLAIM_FLAG) itemInfo.get(p).get(e.getSlot()).info;

        //this.itemInfo.remove(p);
    }

    @Override
    public void clear(Player p) {
        itemInfo.remove(p);
    }

    @Override
    public String getSection() {
        return "Requests";
    }

    @Override
    List<String> getSections() {
        List<String> list = new ArrayList<>();
        for (ITEMS set : ITEMS.values())
            list.add(set.section);
        return list;
    }

    enum ITEMS {
        FLAG("New");

        String section;

        ITEMS(String section) {
            this.section = section;
        }
    }
}
