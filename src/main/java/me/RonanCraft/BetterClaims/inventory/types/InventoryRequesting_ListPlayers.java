package me.RonanCraft.BetterClaims.inventory.types;

import me.RonanCraft.BetterClaims.claims.Claim;
import me.RonanCraft.BetterClaims.claims.ClaimData;
import me.RonanCraft.BetterClaims.inventory.ITEM_TYPE;
import me.RonanCraft.BetterClaims.inventory.PueblosInvLoader;
import me.RonanCraft.BetterClaims.inventory.PueblosInv_MultiClaim;
import me.RonanCraft.BetterClaims.inventory.PueblosItem;
import me.RonanCraft.BetterClaims.resources.helper.HelperClaim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryRequesting_ListPlayers extends PueblosInvLoader implements PueblosInv_MultiClaim {

    private final HashMap<Player, HashMap<Integer, PueblosItem>> itemInfo = new HashMap<>();
    private final HashMap<Player, List<ClaimData>> claims = new HashMap<>();

    @Override
    public Inventory open(Player p, List<ClaimData> claimData) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, getTitle(p, claimData));

        addBorder(inv);

        HashMap<Integer, PueblosItem> itemInfo = new HashMap<>();
        for (ClaimData claim : claimData) {
            ItemStack item;
            if (!claim.hasRequestFrom(p))
                item = getItem(ITEMS.NEW.section, p, claim);
            else
                item = getItem(ITEMS.REQUESTED.section, p, claim);
            int slot = inv.firstEmpty();
            inv.setItem(slot, item);
            itemInfo.put(slot, new PueblosItem(item, ITEM_TYPE.NORMAL, claim));
        }
        this.itemInfo.put(p, itemInfo);
        this.claims.put(p, claimData);
        p.openInventory(inv);
        return inv;
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!this.itemInfo.containsKey(p) || !this.claims.containsKey(p) || checkItems(e, itemInfo.get(p)))
            return;

        Claim claim = (Claim) itemInfo.get(p).get(e.getSlot()).info;
        if (HelperClaim.requestJoin(p, claim))
            p.closeInventory();
    }

    @Override
    public void clear(Player p) {
        itemInfo.remove(p);
    }

    @Override
    protected String getSection() {
        return "Requesting";
    }

    @Override
    protected List<String> getSections() {
        List<String> list = new ArrayList<>();
        for (ITEMS set : ITEMS.values())
            list.add(set.section);
        return list;
    }

    private enum ITEMS {
        NEW("New"),
        REQUESTED("Requested");

        String section;

        ITEMS(String section) {
            this.section = section;
        }
    }
}
