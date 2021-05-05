package me.RonanCraft.Pueblos.inventory.types;

import me.RonanCraft.Pueblos.inventory.*;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_FLAG_MEMBER;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import me.RonanCraft.Pueblos.resources.tools.CONFIRMATION_TYPE;
import me.RonanCraft.Pueblos.resources.tools.Confirmation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryClaimMember extends PueblosInvLoader implements PueblosInv_Member {

    private final HashMap<Player, HashMap<Integer, PueblosItem>> itemInfo = new HashMap<>();
    private final HashMap<Player, ClaimMember> member = new HashMap<>();

    @Override
    public Inventory open(Player p, ClaimMember member) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, getTitle(p, member));

        addBorder(inv);
        HashMap<Integer, PueblosItem> itemInfo = new HashMap<>();

        addButtonBack(inv, p, itemInfo, PueblosInventory.MEMBER, member.claim);

        for (ITEMS i : ITEMS.values()) {
            int slot = i.slot;
            if (slot == 0)
                continue;
            ItemStack item = getItem(i.section, p, member);
            inv.setItem(slot, item);
            itemInfo.put(slot, new PueblosItem(item, ITEM_TYPE.NORMAL, i));
        }

        //Flags
        int slot = 18;
        for (CLAIM_FLAG_MEMBER flag : CLAIM_FLAG_MEMBER.values()) {
            slot = getNextSlot(slot, inv);
            if (slot == -1)
                break;
            ItemStack item;
            if ((Boolean) member.getFlags().getOrDefault(flag, flag.getDefault()))
                item = getItem(ITEMS.FLAG_ENABLED.section, p, new Object[]{member, flag});
            else
                item = getItem(ITEMS.FLAG_DISABLED.section, p, new Object[]{member, flag});
            inv.setItem(slot, item);
            itemInfo.put(slot, new PueblosItem(item, ITEM_TYPE.NORMAL, flag));
        }
        this.itemInfo.put(p, itemInfo);
        this.member.put(p, member);
        p.openInventory(inv);
        return inv;
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @Override
    public void clickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (    !itemInfo.containsKey(p)
                || !member.containsKey(p)
                || checkItems(e, itemInfo.get(p))
                || !itemInfo.get(p).containsKey(e.getSlot()))
            return;

        ClaimMember member = this.member.get(p);
        if (itemInfo.get(p).get(e.getSlot()).info instanceof ITEMS) {
            ITEMS item = (ITEMS) itemInfo.get(p).get(e.getSlot()).info;
            switch (item) {
                case REMOVE:
                    PueblosInventory.CONFIRM.open(p, new Confirmation(CONFIRMATION_TYPE.MEMBER_REMOVE, p, member), false);
                    //HelperClaim.removeMember(p, member);
                    //goBack(PueblosInventory.MEMBERS, p, member.claim);
            }
        } else if (itemInfo.get(p).get(e.getSlot()).info instanceof CLAIM_FLAG_MEMBER) {
            CLAIM_FLAG_MEMBER flag = (CLAIM_FLAG_MEMBER) itemInfo.get(p).get(e.getSlot()).info;
            Object current_value = member.getFlags().getOrDefault(flag, flag.getDefault());
            this.member.get(p).setFlag(flag, flag.alter(current_value), true);
            PueblosInventory.MEMBER.open(p, this.member.get(p), false);
        }
    }

    @Override
    public void clear(Player p) {
        itemInfo.remove(p);
        member.remove(p);
    }

    @Override
    public String getSection() {
        return "Member";
    }

    @Override
    public List<String> getSections() {
        List<String> list = new ArrayList<>();
        for (ITEMS set : ITEMS.values())
            list.add(set.section);
        return list;
    }

    private enum ITEMS {
        MEMBER("Member", 13),
        FLAG_DISABLED("Flag.Disabled", 0),
        FLAG_ENABLED("Flag.Enabled", 0),
        REMOVE("Remove", 16);

        String section;
        int slot;

        ITEMS(String section, int slot) {
            this.section = section;
            this.slot = slot;
        }
    }
}
