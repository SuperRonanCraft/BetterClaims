package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimRequest;
import me.RonanCraft.Pueblos.resources.tools.CONFIRMATION_TYPE;
import me.RonanCraft.Pueblos.resources.tools.Confirmation;
import me.RonanCraft.Pueblos.resources.tools.HelperClaim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryRequests extends PueblosInvLoader implements PueblosInv_Claim {

    private final HashMap<Player, HashMap<Integer, PueblosItem>> itemInfo = new HashMap<>();
    private final HashMap<Player, Claim> claim = new HashMap<>();

    @Override
    public Inventory open(Player p, Claim claim) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, getTitle(p, claim));

        HashMap<Integer, PueblosItem> itemInfo = new HashMap<>();

        addBorder(inv);

        addButtonBack(inv, p, itemInfo, PueblosInventory.REQUESTS, claim);

        int slot = 18;
        for (ClaimRequest request : claim.getRequests()) {
            slot = getNextSlot(slot, inv);
            if (slot == -1)
                break;
            ItemStack item = getItem(ITEMS.REQUEST.section, p, request);
            inv.setItem(slot, item);
            itemInfo.put(slot, new PueblosItem(item, ITEM_TYPE.NORMAL, request));
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

        if (!itemInfo.get(p).containsKey(e.getSlot())) //Not an item we care about
            return;

        ClaimRequest request = (ClaimRequest) itemInfo.get(p).get(e.getSlot()).info;
        if (e.getClick().isLeftClick()) { //Accept
            HelperClaim.requestAction(true, p, request);
            PueblosInventory.REQUESTS.open(p, claim.get(p), false);
        } else if (e.getClick().isRightClick()) { //Decline
            PueblosInventory.CONFIRM.open(p, new Confirmation(CONFIRMATION_TYPE.REQUEST_DECLINE, p, request), false);
            //HelperClaim.requestAction(false, p, request);
            //PueblosInventory.REQUESTS.open(p, claim.get(p), false);
        }
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

    private enum ITEMS {
        REQUEST("Request");

        String section;

        ITEMS(String section) {
            this.section = section;
        }
    }
}
