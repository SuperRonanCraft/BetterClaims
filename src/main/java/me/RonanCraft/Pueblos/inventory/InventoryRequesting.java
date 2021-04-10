package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.tools.HelperClaim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryRequesting extends PueblosInvLoader implements PueblosInv_Requesting {

    private final HashMap<Player, HashMap<Integer, PueblosItem>> itemInfo = new HashMap<>();
    private final HashMap<Player, List<Claim>> claims = new HashMap<>();

    @Override
    public Inventory open(Player p, List<Claim> claims) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, getTitle(p, claims));

        addBorder(inv);

        HashMap<Integer, PueblosItem> itemInfo = new HashMap<>();
        for (Claim claim : claims) {
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
        this.claims.put(p, claims);
        p.openInventory(inv);
        return inv;
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!this.itemInfo.containsKey(p) || !this.claims.containsKey(p) || checkItems(e, itemInfo.get(p)))
            return;

        Claim claim = (Claim) itemInfo.get(p).get(e.getSlot()).info;
        if (HelperClaim.requestJoin(p, claim))
            p.closeInventory();
    }

    @Override
    public void clear(Player p) {
        itemInfo.remove(p);
    }

    @Override
    public String getSection() {
        return "Requesting";
    }

    @Override
    List<String> getSections() {
        List<String> list = new ArrayList<>();
        for (ITEMS set : ITEMS.values())
            list.add(set.section);
        return list;
    }

    private enum ITEMS {
        NEW("New"),
        REQUESTED("Requested");

        String section;

        ITEMS(String section) {
            this.section = section;
        }
    }
}
