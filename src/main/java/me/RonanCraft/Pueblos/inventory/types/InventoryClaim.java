package me.RonanCraft.Pueblos.inventory.types;

import me.RonanCraft.Pueblos.inventory.*;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_PERMISSION_LEVEL;
import me.RonanCraft.Pueblos.resources.claims.ClaimMain;
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
    private final HashMap<Player, ClaimMain> claim = new HashMap<>();

    @Override
    public Inventory open(Player p, ClaimMain claim) {
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
        ClaimMain claim = this.claim.get(p);
        switch (setting) {
            case TELEPORT: HelperClaim.teleportTo(p, claim); p.closeInventory(); break;
            case DELETE: PueblosInventory.CONFIRM.open(p, new Confirmation(CONFIRMATION_TYPE.CLAIM_DELETE, p, claim), false); break;
            case STATS: HelperClaim.sendClaimInfo(p, claim); p.closeInventory(); break;
            default: setting.inv.open(p, claim, false);
        }
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
        MEMBERS(20,     null,                           PueblosInventory.MEMBERS, ITEMS.MEMBERS, null),
        FLAGS(22,       null,                           PueblosInventory.FLAGS, ITEMS.FLAGS_ALLOWED, ITEMS.FLAGS_DISALLOWED),
        REQUESTS(24,    null,                           PueblosInventory.REQUESTS, ITEMS.REQUESTS_ALLOWED, ITEMS.REQUESTS_DISALLOWED),
        TELEPORT(16,    null,                           null, ITEMS.TELEPORT, null),
        DELETE(10,      CLAIM_PERMISSION_LEVEL.OWNER,   null, ITEMS.DELETE, null),
        STATS(28,       null,                           null, ITEMS.STATS, null);

        int slot;
        CLAIM_PERMISSION_LEVEL claim_permission_level;
        PueblosInventory inv;
        ITEMS allowed;
        ITEMS disallowed;

        CLAIM_SETTINGS(int slot, CLAIM_PERMISSION_LEVEL claim_permission_level, PueblosInventory inv, ITEMS allowed, ITEMS disallowed) {
            this.slot = slot;
            this.claim_permission_level = claim_permission_level;
            this.inv = inv;
            this.allowed = allowed;
            this.disallowed = disallowed;
        }

        ITEMS getItem(Player p, ClaimMain claim) {
            if (disallowed != null) {
                if (inv.isAllowed(p, claim))
                    return allowed;
                return disallowed;
            }
            return allowed;
        }
    }

    private enum ITEMS {
        STATS("Statistics"),
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
