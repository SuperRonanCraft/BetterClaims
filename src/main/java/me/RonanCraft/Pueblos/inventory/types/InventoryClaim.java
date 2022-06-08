package me.RonanCraft.Pueblos.inventory.types;

import me.RonanCraft.Pueblos.inventory.*;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMain;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_PERMISSION_LEVEL;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
import me.RonanCraft.Pueblos.resources.tools.CONFIRMATION_TYPE;
import me.RonanCraft.Pueblos.resources.tools.Confirmation;
import me.RonanCraft.Pueblos.resources.tools.HelperClaim;
import me.RonanCraft.Pueblos.resources.tools.visual.Visualization;
import me.RonanCraft.Pueblos.resources.tools.visual.VisualizationType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

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
        for (CLAIM_SETTINGS setting : CLAIM_SETTINGS.values()) {
            if (!claim.checkPermLevel(p, setting.claim_permission_level))
                continue;
            if (setting.permission != null && !setting.permission.check(p))
                continue;
            if (setting == CLAIM_SETTINGS.CHILD_CLAIM && (claim.isChild() || ((ClaimMain) claim).getChildren().isEmpty()))
                continue;
            ItemStack item = getItem(setting.getItem(p, claim).section, p, claim);
            inv.setItem(setting.slot, item);
            itemInfo.put(setting.slot, new PueblosItem(item, ITEM_TYPE.NORMAL, setting));
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
        switch (setting) {
            case TELEPORT: HelperClaim.teleportTo(p, claim); p.closeInventory(); break;
            case DELETE: PueblosInventory.CONFIRM.open(p, new Confirmation(CONFIRMATION_TYPE.CLAIM_DELETE, p, claim), false); break;
            case STATS: HelperClaim.sendClaimInfo(p, claim); p.closeInventory(); break;
            case CHILD_CLAIM: PueblosInventory.CLAIM_SELECT.open(p, ((ClaimMain) claim).getChildren(), false); break;
            case VISUALIZE:
                Vector vector = claim.getBoundingBox().getCenter();
                if (p.getLocation().distance(new Location(p.getLocation().getWorld(), vector.getX(), p.getLocation().getBlockY(), vector.getZ())) < 300) {
                    Visualization.fromClaim(claim, p.getLocation().getBlockY(), VisualizationType.CLAIM, p.getLocation()).apply(p);
                    MessagesCore.CLAIM_VISUALIZE.send(p, claim);
                    p.closeInventory();
                } else
                    MessagesCore.TOOFAR.send(p, claim);
                break;
            default:
                if (setting.inv != null)
                    setting.inv.open(p, claim, false);
                else
                    p.sendMessage("Not Yet Implemented!");
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
        MEMBERS(20,     null,   PueblosInventory.MEMBERS, ITEMS.MEMBERS),
        FLAGS(22,       null,   PueblosInventory.FLAGS, ITEMS.FLAGS_ALLOWED, ITEMS.FLAGS_DISALLOWED),
        REQUESTS(24,    null,   PueblosInventory.REQUESTS, ITEMS.REQUESTS_ALLOWED, ITEMS.REQUESTS_DISALLOWED),
        TELEPORT(16,    null,   null, ITEMS.TELEPORT, null, PermissionNodes.TELEPORT),
        DELETE(10,      CLAIM_PERMISSION_LEVEL.OWNER, null, ITEMS.DELETE),
        STATS(28,       null,   null, ITEMS.STATS),
        CHILD_CLAIM(34, null,   null, ITEMS.CHILD_CLAIMS),
        VISUALIZE(40,   null,   null, ITEMS.VISUALIZE),
        ;

        int slot;
        CLAIM_PERMISSION_LEVEL claim_permission_level;
        PueblosInventory inv;
        ITEMS allowed;
        ITEMS disallowed;
        PermissionNodes permission;

        CLAIM_SETTINGS(int slot, CLAIM_PERMISSION_LEVEL claim_per_level, @Nullable PueblosInventory inv, ITEMS allowed, ITEMS disallowed, @Nullable PermissionNodes per) {
            this.slot = slot;
            this.claim_permission_level = claim_per_level;
            this.inv = inv;
            this.allowed = allowed;
            this.disallowed = disallowed;
            this.permission = per;
        }

        CLAIM_SETTINGS(int slot, CLAIM_PERMISSION_LEVEL claim_per_level, @Nullable PueblosInventory inv, ITEMS allowed, ITEMS disallowed) {
            this(slot, claim_per_level, inv, allowed, disallowed, null);
        }

        CLAIM_SETTINGS(int slot, CLAIM_PERMISSION_LEVEL claim_per_level, @Nullable PueblosInventory inv, ITEMS allowed) {
            this(slot, claim_per_level, inv, allowed, null);
        }

        ITEMS getItem(Player p, Claim claim) {
            if (disallowed != null) {
                if (inv != null && inv.isAllowed(p, claim))
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
        DELETE("Delete"),
        CHILD_CLAIMS("SubClaim"),
        VISUALIZE("Visualize"),
        ;

        String section;

        ITEMS(String section) {
            this.section = section;
        }
    }
}
