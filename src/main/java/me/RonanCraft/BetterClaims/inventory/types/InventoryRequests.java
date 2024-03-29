package me.RonanCraft.BetterClaims.inventory.types;

import me.RonanCraft.BetterClaims.inventory.*;
import me.RonanCraft.BetterClaims.claims.ClaimData;
import me.RonanCraft.BetterClaims.claims.data.Claim_Request;
import me.RonanCraft.BetterClaims.inventory.confirmation.CONFIRMATION_TYPE;
import me.RonanCraft.BetterClaims.inventory.confirmation.Confirmation;
import me.RonanCraft.BetterClaims.resources.helper.HelperClaim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryRequests extends ClaimInvLoader implements ClaimInv_Claim {

    private final HashMap<Player, HashMap<Integer, ClaimItem>> itemInfo = new HashMap<>();
    private final HashMap<Player, ClaimData> claim = new HashMap<>();

    @Override
    public Inventory open(Player p, ClaimData claimData) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, getTitle(p, claimData));

        HashMap<Integer, ClaimItem> itemInfo = new HashMap<>();

        addBorder(inv);

        addButtonBack(inv, p, itemInfo, ClaimInventory.REQUESTS, claimData);

        int slot = 18;
        for (Claim_Request request : claimData.getRequests()) {
            slot = getNextSlot(slot, inv);
            if (slot == -1)
                break;
            ItemStack item = getItem(ITEMS.REQUEST.section, p, request);
            inv.setItem(slot, item);
            itemInfo.put(slot, new ClaimItem(item, ITEM_TYPE.NORMAL, request));
        }
        this.itemInfo.put(p, itemInfo);
        this.claim.put(p, claimData);
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

        Claim_Request request = (Claim_Request) itemInfo.get(p).get(e.getSlot()).info;
        if (e.getClick().isLeftClick()) { //Accept
            HelperClaim.requestAction(true, p, request);
            ClaimInventory.REQUESTS.open(p, claim.get(p), false);
        } else if (e.getClick().isRightClick()) { //Decline
            if (e.getClick().isShiftClick()) //Ignore this player from any other requests in all their claims
                ClaimInventory.CONFIRM.open(p, new Confirmation(CONFIRMATION_TYPE.REQUEST_DECLINE_IGNORE, p, request), false);
            else //Decline this request
                ClaimInventory.CONFIRM.open(p, new Confirmation(CONFIRMATION_TYPE.REQUEST_DECLINE, p, request), false);
        }
    }

    @Override
    public void clear(Player p) {
        itemInfo.remove(p);
    }

    @Override
    protected String getSection() {
        return "Requests";
    }

    @Override
    protected List<String> getSections() {
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
