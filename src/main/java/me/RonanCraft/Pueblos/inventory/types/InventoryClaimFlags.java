package me.RonanCraft.Pueblos.inventory.types;

import me.RonanCraft.Pueblos.inventory.*;
import me.RonanCraft.Pueblos.claims.ClaimData;
import me.RonanCraft.Pueblos.claims.enums.CLAIM_FLAG;
import me.RonanCraft.Pueblos.resources.helper.HelperClaim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryClaimFlags extends PueblosInvLoader implements PueblosInv_Claim {

    private final HashMap<Player, HashMap<Integer, PueblosItem>> itemInfo = new HashMap<>();
    private final HashMap<Player, ClaimData> claim = new HashMap<>();

    @Override
    public Inventory open(Player p, ClaimData claimData) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, getTitle(p, claimData));

        HashMap<Integer, PueblosItem> itemInfo = new HashMap<>();

        addBorder(inv);

        addButtonBack(inv, p, itemInfo, PueblosInventory.FLAGS, claimData);

        int slot = 18;
        for (CLAIM_FLAG flag : CLAIM_FLAG.values()) {
            slot = getNextSlot(slot, inv);
            if (slot == -1)
                break;
            ItemStack item;
            if ((Boolean) claimData.getFlags().getFlag(flag))
                item = getItem(ITEMS.FLAG_ENABLED.section, p, new Object[]{claimData, flag});
            else
                item = getItem(ITEMS.FLAG_DISABLED.section, p, new Object[]{claimData, flag});
            inv.setItem(slot, item);
            itemInfo.put(slot, new PueblosItem(item, ITEM_TYPE.NORMAL, flag));
        }
        this.itemInfo.put(p, itemInfo);
        this.claim.put(p, claimData);
        p.openInventory(inv);
        return inv;
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (    !this.itemInfo.containsKey(p)
                || !this.claim.containsKey(p)
                || checkItems(e, itemInfo.get(p))
                || !itemInfo.get(p).containsKey(e.getSlot()))
            return;

        CLAIM_FLAG flag = (CLAIM_FLAG) itemInfo.get(p).get(e.getSlot()).info;
        ClaimData claimData = this.claim.get(p);
        HelperClaim.toggleFlag(p, claimData, flag);
        PueblosInventory.FLAGS.open(p, claimData, false);
        //this.itemInfo.remove(p);
    }

    @Override
    public void clear(Player p) {
        itemInfo.remove(p);
    }

    @Override
    protected String getSection() {
        return "Flags";
    }

    @Override
    protected List<String> getSections() {
        List<String> list = new ArrayList<>();
        for (ITEMS set : ITEMS.values())
            list.add(set.section);
        return list;
    }

    private enum ITEMS {
        FLAG_ENABLED("Enabled"),
        FLAG_DISABLED("Disabled");

        String section;

        ITEMS(String section) {
            this.section = section;
        }
    }
}
