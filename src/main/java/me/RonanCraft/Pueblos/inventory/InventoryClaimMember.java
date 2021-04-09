package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.claims.CLAIM_FLAG_MEMBER;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import me.RonanCraft.Pueblos.resources.files.FileOther;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

        int slot = 13;
        inv.setItem(slot, getItem(ITEMS.MEMBER.section, p, member));

        //Flags
        slot = slot + 5;
        for (CLAIM_FLAG_MEMBER flag : CLAIM_FLAG_MEMBER.values()) {
            ItemStack item;
            if ((Boolean) member.getFlags().getOrDefault(flag, flag.getDefault()))
                item = getItem(ITEMS.ENABLED.section, p, new Object[]{member, flag});
            else
                item = getItem(ITEMS.DISABLED.section, p, new Object[]{member, flag});
            slot = getNextSlot(slot, inv);
            if (slot == -1)
                break;
            inv.setItem(slot, item);
            itemInfo.put(slot, new PueblosItem(item, ITEM_TYPE.NORMAL, flag));
        }
        this.itemInfo.put(p, itemInfo);
        this.member.put(p, member);
        p.openInventory(inv);
        return inv;
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!itemInfo.containsKey(p) || checkItems(e, itemInfo.get(p)) || !itemInfo.get(p).containsKey(e.getSlot()))
            return;

        CLAIM_FLAG_MEMBER flag = (CLAIM_FLAG_MEMBER) itemInfo.get(p).get(e.getSlot()).info;
        ClaimMember member = this.member.get(p);
        Object current_value = member.getFlags().getOrDefault(flag, flag.getDefault());
        this.member.get(p).setFlag(flag, flag.alter(current_value), true);
        PueblosInventory.MEMBER.open(p, this.member.get(p), false);
        //this.itemInfo.remove(p);
        //this.member.remove(p);
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

    enum ITEMS {
        MEMBER("Member"),
        DISABLED("Flag.Disabled"),
        ENABLED("Flag.Enabled");

        String section;

        ITEMS(String section) {
            this.section = section;
        }
    }
}
