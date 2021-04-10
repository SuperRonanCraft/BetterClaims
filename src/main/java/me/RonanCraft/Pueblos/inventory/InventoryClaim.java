package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryClaim extends PueblosInvLoader implements PueblosInv_Claim {

    private final HashMap<Player, HashMap<Integer, PueblosItem>> itemInfo = new HashMap<>();
    private final HashMap<Player, Claim> claim = new HashMap<>();

    @Override
    public Inventory open(Player p, Claim claim) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, getTitle(p, claim));

        addBorder(inv);

        HashMap<Integer, PueblosItem> itemInfo = new HashMap<>();
        for (CLAIM_SETTINGS set : CLAIM_SETTINGS.values()) {
            ItemStack item = getItem(set.getItem(p, claim).section, p, claim);

            inv.setItem(set.slot, item);
            itemInfo.put(set.slot, new PueblosItem(item, ITEM_TYPE.NORMAL, set));
        }
        this.itemInfo.put(p, itemInfo);
        this.claim.put(p, claim);
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

        CLAIM_SETTINGS setting = (CLAIM_SETTINGS) itemInfo.get(p).get(e.getSlot()).info;
        Claim claim = this.claim.get(p);
        setting.inv.open(p, claim, false);
    }

    @Override
    public void clear(Player p) {
        itemInfo.remove(p);
    }

    @Override
    public String getSection() {
        return "Claim";
    }

    @Override
    List<String> getSections() {
        List<String> list = new ArrayList<>();
        for (ITEMS set : ITEMS.values())
            list.add(set.section);
        return list;
    }

    private enum CLAIM_SETTINGS {
        MEMBERS("Members", 20, PueblosInventory.MEMBERS, ITEMS.MEMBERS, null),
        FLAGS("Flags", 22, PueblosInventory.FLAGS, ITEMS.FLAGS_ALLOWED, ITEMS.FLAGS_DISALLOWED),
        REQUESTS("Requests", 24, PueblosInventory.REQUESTS, ITEMS.REQUESTS_ALLOWED, ITEMS.REQUESTS_DISALLOWED);

        String section;
        int slot;
        PueblosInventory inv;
        ITEMS allowed;
        ITEMS disallowed;

        CLAIM_SETTINGS(String section, int slot, PueblosInventory inv, @Nonnull ITEMS allowed, ITEMS disallowed) {
            this.section = section;
            this.slot = slot;
            this.inv = inv;
            this.allowed = allowed;
            this.disallowed = disallowed;
        }

        ITEMS getItem(Player p, Claim claim) {
            if (disallowed != null) {
                if (inv.isAllowed(p, claim))
                    return allowed;
                return disallowed;
            }
            return allowed;
        }
    }

    private enum ITEMS {
        MEMBERS("Members"),
        FLAGS_ALLOWED("Flags.Allowed"),
        FLAGS_DISALLOWED("Flags.Disallowed"),
        REQUESTS_ALLOWED("Requests.Allowed"),
        REQUESTS_DISALLOWED("Requests.Disallowed");

        String section;

        ITEMS(String section) {
            this.section = section;
        }
    }
}
