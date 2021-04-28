package me.RonanCraft.Pueblos.inventory.types;

import me.RonanCraft.Pueblos.inventory.*;
import me.RonanCraft.Pueblos.resources.claims.CLAIM_PERMISSION_LEVEL;
import me.RonanCraft.Pueblos.resources.claims.Claim;
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

public class InventoryClaim extends PueblosInvLoader implements PueblosInv_Claim {

    private final HashMap<Player, HashMap<Integer, PueblosItem>> itemInfo = new HashMap<>();
    private final HashMap<Player, Claim> claim = new HashMap<>();

    @Override
    public Inventory open(Player p, Claim claim) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, getTitle(p, claim));

        addBorder(inv);

        HashMap<Integer, PueblosItem> itemInfo = new HashMap<>();
        for (CLAIM_SETTINGS set : CLAIM_SETTINGS.values()) {
            if (!claim.checkPermLevel(p, set.claim_permission_level))
                continue;
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
        if (!this.itemInfo.containsKey(p)
                || !this.claim.containsKey(p)
                || checkItems(e, itemInfo.get(p))
                || !itemInfo.get(p).containsKey(e.getSlot()))
            return;

        CLAIM_SETTINGS setting = (CLAIM_SETTINGS) itemInfo.get(p).get(e.getSlot()).info;
        Claim claim = this.claim.get(p);
        if (setting == CLAIM_SETTINGS.TELEPORT) {
            HelperClaim.teleportTo(p, claim);
        } else if (setting == CLAIM_SETTINGS.DELETE) {
            PueblosInventory.CONFIRM.open(p, new Confirmation(CONFIRMATION_TYPE.CLAIM_DELETE, p, claim), false);
        } else
            setting.inv.open(p, claim, false);
    }

    @Override
    public void clear(Player p) {
        itemInfo.remove(p);
    }

    @Override
    protected String getSection() {
        return "Claim";
    }

    @Override
    protected List<String> getSections() {
        List<String> list = new ArrayList<>();
        for (ITEMS set : ITEMS.values())
            list.add(set.section);
        return list;
    }

    private enum CLAIM_SETTINGS {
        MEMBERS("Members", 20,      null,                           PueblosInventory.MEMBERS, ITEMS.MEMBERS, null),
        FLAGS("Flags", 22,          null,                           PueblosInventory.FLAGS, ITEMS.FLAGS_ALLOWED, ITEMS.FLAGS_DISALLOWED),
        REQUESTS("Requests", 24,    null,                           PueblosInventory.REQUESTS, ITEMS.REQUESTS_ALLOWED, ITEMS.REQUESTS_DISALLOWED),
        TELEPORT("Teleport", 16,    null,                           null, ITEMS.TELEPORT, null),
        DELETE("Delete", 10,        CLAIM_PERMISSION_LEVEL.OWNER,   null, ITEMS.DELETE, null);

        String section;
        int slot;
        CLAIM_PERMISSION_LEVEL claim_permission_level;
        PueblosInventory inv;
        ITEMS allowed;
        ITEMS disallowed;

        CLAIM_SETTINGS(String section, int slot, CLAIM_PERMISSION_LEVEL claim_permission_level, PueblosInventory inv, ITEMS allowed, ITEMS disallowed) {
            this.section = section;
            this.slot = slot;
            this.claim_permission_level = claim_permission_level;
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
        REQUESTS_DISALLOWED("Requests.Disallowed"),
        TELEPORT("Teleport"),
        DELETE("Delete");

        String section;

        ITEMS(String section) {
            this.section = section;
        }
    }
}
