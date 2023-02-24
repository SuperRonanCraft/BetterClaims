package me.RonanCraft.BetterClaims.inventory.types;

import me.RonanCraft.BetterClaims.inventory.*;
import me.RonanCraft.BetterClaims.resources.PermissionNodes;
import me.RonanCraft.BetterClaims.claims.ClaimData;
import me.RonanCraft.BetterClaims.claims.Claim;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_PERMISSION_LEVEL;
import me.RonanCraft.BetterClaims.resources.messages.MessagesCore;
import me.RonanCraft.BetterClaims.inventory.confirmation.CONFIRMATION_TYPE;
import me.RonanCraft.BetterClaims.inventory.confirmation.Confirmation;
import me.RonanCraft.BetterClaims.resources.helper.HelperClaim;
import me.RonanCraft.BetterClaims.resources.visualization.Visualization;
import me.RonanCraft.BetterClaims.resources.visualization.VisualizationType;
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

public class InventoryClaim extends ClaimInvLoader implements ClaimInv_Claim {

    private final HashMap<Player, HashMap<Integer, ClaimItem>> itemInfo = new HashMap<>();
    private final HashMap<Player, ClaimData> claim = new HashMap<>();

    @Override
    public Inventory open(Player p, ClaimData claimData) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, getTitle(p, claimData));

        addBorder(inv);

        HashMap<Integer, ClaimItem> itemInfo = new HashMap<>();
        for (CLAIM_SETTINGS setting : CLAIM_SETTINGS.values()) {
            if (!claimData.checkPermLevel(p, setting.claim_permission_level))
                continue;
            if (setting.permission != null && !setting.permission.check(p))
                continue;
            if (setting == CLAIM_SETTINGS.CHILD_CLAIM && (claimData.isChild() || ((Claim) claimData).getChildren().isEmpty()))
                continue;
            if (claimData.isAdminClaim()) {
                switch (setting) {
                    case FLAGS:
                    case REQUESTS:
                    case MEMBERS: continue;
                }
            }
            ItemStack item = getItem(setting.getItem(p, claimData).section, p, claimData);
            inv.setItem(setting.slot, item);
            itemInfo.put(setting.slot, new ClaimItem(item, ITEM_TYPE.NORMAL, setting));
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

        CLAIM_SETTINGS setting = (CLAIM_SETTINGS) itemInfo.get(p).get(e.getSlot()).info;
        ClaimData claimData = this.claim.get(p);
        switch (setting) {
            case TELEPORT: HelperClaim.teleportTo(p, claimData); p.closeInventory(); break;
            case DELETE: ClaimInventory.CONFIRM.open(p, new Confirmation(CONFIRMATION_TYPE.CLAIM_DELETE, p, claimData), false); break;
            case STATS: HelperClaim.sendClaimInfo(p, claimData); p.closeInventory(); break;
            case CHILD_CLAIM: ClaimInventory.CLAIM_SELECT.open(p, ((Claim) claimData).getChildren(), false); break;
            case VISUALIZE:
                Vector vector = claimData.getBoundingBox().getCenter();
                if (p.getLocation().distance(new Location(p.getLocation().getWorld(), vector.getX(), p.getLocation().getBlockY(), vector.getZ())) < 300) {
                    Visualization.fromClaim(claimData, p.getLocation().getBlockY(), VisualizationType.CLAIM, p.getLocation()).apply(p);
                    MessagesCore.CLAIM_VISUALIZE.send(p, claimData);
                    p.closeInventory();
                } else
                    MessagesCore.TOOFAR.send(p, claimData);
                break;
            default:
                if (setting.inv != null)
                    setting.inv.open(p, claimData, false);
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
        MEMBERS(20,     null,   ClaimInventory.MEMBERS, ITEMS.MEMBERS),
        FLAGS(22,       null,   ClaimInventory.FLAGS, ITEMS.FLAGS_ALLOWED, ITEMS.FLAGS_DISALLOWED),
        REQUESTS(24,    null,   ClaimInventory.REQUESTS, ITEMS.REQUESTS_ALLOWED, ITEMS.REQUESTS_DISALLOWED),
        TELEPORT(16,    null,   null, ITEMS.TELEPORT, null, PermissionNodes.TELEPORT),
        DELETE(10,      CLAIM_PERMISSION_LEVEL.OWNER, null, ITEMS.DELETE),
        STATS(28,       null,   null, ITEMS.STATS),
        CHILD_CLAIM(34, null,   null, ITEMS.CHILD_CLAIMS),
        VISUALIZE(40,   null,   null, ITEMS.VISUALIZE),
        ;

        int slot;
        CLAIM_PERMISSION_LEVEL claim_permission_level;
        ClaimInventory inv;
        ITEMS allowed;
        ITEMS disallowed;
        PermissionNodes permission;

        CLAIM_SETTINGS(int slot, CLAIM_PERMISSION_LEVEL claim_per_level, @Nullable ClaimInventory inv, ITEMS allowed, ITEMS disallowed, @Nullable PermissionNodes per) {
            this.slot = slot;
            this.claim_permission_level = claim_per_level;
            this.inv = inv;
            this.allowed = allowed;
            this.disallowed = disallowed;
            this.permission = per;
        }

        CLAIM_SETTINGS(int slot, CLAIM_PERMISSION_LEVEL claim_per_level, @Nullable ClaimInventory inv, ITEMS allowed, ITEMS disallowed) {
            this(slot, claim_per_level, inv, allowed, disallowed, null);
        }

        CLAIM_SETTINGS(int slot, CLAIM_PERMISSION_LEVEL claim_per_level, @Nullable ClaimInventory inv, ITEMS allowed) {
            this(slot, claim_per_level, inv, allowed, null);
        }

        ITEMS getItem(Player p, ClaimData claimData) {
            if (disallowed != null) {
                if (inv != null && inv.isAllowed(p, claimData))
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
