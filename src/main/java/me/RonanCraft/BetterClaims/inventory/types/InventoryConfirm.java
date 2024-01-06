package me.RonanCraft.BetterClaims.inventory.types;

import me.RonanCraft.BetterClaims.claims.Claim_Child;
import me.RonanCraft.BetterClaims.inventory.*;
import me.RonanCraft.BetterClaims.claims.Claim;
import me.RonanCraft.BetterClaims.claims.data.members.Member;
import me.RonanCraft.BetterClaims.claims.data.Claim_Request;
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

public class InventoryConfirm extends ClaimInvLoader implements ClaimInv_Confirming {

    private final HashMap<Player, HashMap<Integer, ClaimItem>> itemInfo = new HashMap<>();
    private final HashMap<Player, Confirmation> confirmation = new HashMap<>();

    @Override
    public Inventory open(Player p, Confirmation confirmation) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, getTitle(p, confirmation));

        addBorder(inv);

        HashMap<Integer, ClaimItem> itemInfo = new HashMap<>();

        addButtonBack(inv, p, itemInfo, ClaimInventory.CONFIRM, confirmation.info);

        for (ITEMS i : ITEMS.values()) {
            //Accept button
            ItemStack item = getItem(i.section, p, confirmation);
            int slot = i.slot;
            inv.setItem(slot, item);
            itemInfo.put(slot, new ClaimItem(item, ITEM_TYPE.NORMAL, i));
        }

        this.itemInfo.put(p, itemInfo);
        this.confirmation.put(p, confirmation);
        p.openInventory(inv);
        return inv;
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (    !itemInfo.containsKey(p)
                || !confirmation.containsKey(p)
                || checkItems(e, itemInfo.get(p))
                || !itemInfo.get(p).containsKey(e.getSlot()))
            return;

        Confirmation confirmation = this.confirmation.get(p);
        ITEMS buttonPressed = (ITEMS) this.itemInfo.get(p).get(e.getSlot()).info;
        switch (buttonPressed) {
            case ACCEPT:
                //p.sendMessage("Accepted!");
                switch (confirmation.type) {
                    case CLAIM_LEAVE:
                        HelperClaim.leaveClaim(p, (Member) confirmation.info);
                        p.closeInventory();
                        break;
                    case MEMBER_REMOVE:
                        HelperClaim.removeMember(p, (Member) confirmation.info);
                        break;
                    case REQUEST_DECLINE:
                        HelperClaim.requestAction(false, p, (Claim_Request) confirmation.info);
                        break;
                    case CLAIM_DELETE:
                        if (confirmation.info instanceof Claim)
                            HelperClaim.deleteClaim(p, (Claim) confirmation.info);
                        else
                            HelperClaim.deleteClaimChild(p, (Claim_Child) confirmation.info);
                        p.closeInventory();
                        return;
                }
                goBack(p, this.itemInfo.get(p));
                break;
            case CANCEL:
                //p.sendMessage("Declined?");
                goBack(p, this.itemInfo.get(p));
                break;
        }
    }

    @Override
    public void clear(Player p) {

    }

    @Override
    protected String getSection() {
        return "Confirm";
    }

    @Override
    protected List<String> getSections() {
        List<String> list = new ArrayList<>();
        for (ITEMS i : ITEMS.values())
            list.add(i.section);
        return list;
    }

    private enum ITEMS {
        ACCEPT("Accept", 22),
        CANCEL("Cancel", 16);

        String section;
        int slot;

        ITEMS(String section, int slot) {
            this.section = section;
            this.slot = slot;
        }
    }
}
