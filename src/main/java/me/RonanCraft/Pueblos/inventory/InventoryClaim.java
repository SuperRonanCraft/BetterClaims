package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.apache.commons.lang.StringUtils;
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
            ItemStack item = getItem(set.section, p, claim);
            //setTitle(item, p, StringUtils.capitalize(setting.name().toLowerCase()));
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
        if (!this.itemInfo.containsKey(p) || !this.claim.containsKey(p) || checkItems(e, itemInfo.get(p)))
            return;

        CLAIM_SETTINGS setting = (CLAIM_SETTINGS) itemInfo.get(p).get(e.getSlot()).info;
        Claim claim = this.claim.get(p);
        switch (setting) {
            case MEMBERS: PueblosInventory.MEMBERS.open(p, claim, false); this.itemInfo.remove(p); break;
            case FLAGS:

                break;
            default: p.sendMessage("Not Yet Supported!");
        }
        //this.itemInfo.remove(p);
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
        for (CLAIM_SETTINGS set : CLAIM_SETTINGS.values())
            list.add(set.section);
        return list;
    }

    enum CLAIM_SETTINGS {
        MEMBERS("Members", 21),
        FLAGS("Flags", 23);

        String section;
        int slot;

        CLAIM_SETTINGS(String section, int slot) {
            this.section = section;
            this.slot = slot;
        }
    }
}
