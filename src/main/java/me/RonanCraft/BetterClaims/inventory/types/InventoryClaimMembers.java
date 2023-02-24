package me.RonanCraft.BetterClaims.inventory.types;

import me.RonanCraft.BetterClaims.inventory.*;
import me.RonanCraft.BetterClaims.claims.ClaimData;
import me.RonanCraft.BetterClaims.claims.data.members.Member;
import me.RonanCraft.BetterClaims.inventory.confirmation.CONFIRMATION_TYPE;
import me.RonanCraft.BetterClaims.inventory.confirmation.Confirmation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryClaimMembers extends PueblosInvLoader implements PueblosInv_Claim {

    private final HashMap<Player, HashMap<Integer, PueblosItem>> itemInfo = new HashMap<>();
    private final HashMap<Player, ClaimData> claim = new HashMap<>();

    @Override
    public Inventory open(Player p, ClaimData claimData) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, getTitle(p, claimData));

        addBorder(inv);

        HashMap<Integer, PueblosItem> itemInfo = new HashMap<>();

        //Base Items
        for (ITEMS i : ITEMS.values()) {
            if (i.slot == 0) //Slot 0 = Disabled
                continue;
            if (i == ITEMS.LEAVE && claimData.isOwner(p))
                continue;
            ItemStack _item = getItem(i.section, p, claimData);
            inv.setItem(i.slot, _item);
            itemInfo.put(i.slot, new PueblosItem(_item, ITEM_TYPE.NORMAL, i));
        }

        addButtonBack(inv, p, itemInfo, PueblosInventory.MEMBERS, claimData);

        int slot = 18;

        ITEMS _i = claimData.isOwner(p) ? ITEMS.MEMBER_EDIT : ITEMS.MEMBER_DISALLOWED;
        for (Member member : claimData.getMembers()) {
            slot = getNextSlot(slot, inv);
            if (slot == -1)
                break;
            ItemStack item = getItem(_i.section, p, member);
            inv.setItem(slot, item);
            itemInfo.put(slot, new PueblosItem(item, ITEM_TYPE.NORMAL, member));
        }
        this.itemInfo.put(p, itemInfo);
        this.claim.put(p, claimData);
        p.openInventory(inv);
        return inv;
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (    !itemInfo.containsKey(p)
                || !claim.containsKey(p)
                || checkItems(e, itemInfo.get(p))
                || !itemInfo.get(p).containsKey(e.getSlot()))
            return;

        if (itemInfo.get(p).get(e.getSlot()).info instanceof ITEMS) {
            ITEMS item = (ITEMS) itemInfo.get(p).get(e.getSlot()).info;
            switch (item) {
                case REQUEST:
                    PueblosInventory.REQUESTS.open(p, claim.get(p), false);
                case LEAVE:
                    PueblosInventory.CONFIRM.open(p, new Confirmation(CONFIRMATION_TYPE.CLAIM_LEAVE, p, claim.get(p).getMember(p)), false);
            }
        } else if (itemInfo.get(p).get(e.getSlot()).info instanceof Member) {
            Member member = (Member) itemInfo.get(p).get(e.getSlot()).info;
            //this.itemInfo.remove(p);
            PueblosInventory.MEMBER.open(p, member, false);
        }
    }

    @Override
    public void clear(Player p) {
        itemInfo.remove(p);
    }

    public String getSection() {
        return "Members";
    }

    @Override
    public List<String> getSections() {
        List<String> list = new ArrayList<>();
        for (ITEMS set : ITEMS.values())
            list.add(set.section);
        return list;
    }

    private enum ITEMS {
        MEMBER_EDIT("Member.Edit", 0),
        MEMBER_DISALLOWED("Member.Disallowed", 0),
        OWNER("Owner", 13),
        REQUEST("Requests", 16),
        LEAVE("Leave", 15);

        String section;
        int slot;

        ITEMS(String section, int slot) {
            this.section = section;
            this.slot = slot;
        }
    }
}
