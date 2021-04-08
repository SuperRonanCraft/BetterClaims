package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.claims.CLAIM_FLAG_MEMBER;
import me.RonanCraft.Pueblos.resources.claims.CLAIM_SETTINGS;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryClaim implements PueblosInv_Claim {

    private final HashMap<Player, HashMap<Integer, PueblosItem>> itemInfo = new HashMap<>();
    private final HashMap<Player, Claim> claim = new HashMap<>();

    @Override
    public Inventory open(Player p, Claim claim) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, "Claim " + claim.getName());

        addBorder(inv);

        HashMap<Integer, PueblosItem> itemInfo = new HashMap<>();
        for (CLAIM_SETTINGS setting : CLAIM_SETTINGS.values()) {
            int slot = inv.firstEmpty();
            ItemStack item = new ItemStack(Material.DIAMOND);
            setTitle(item, p, StringUtils.capitalize(setting.name().toLowerCase()));
            inv.setItem(slot, item);
            itemInfo.put(slot, new PueblosItem(item, setting));
        }
        this.itemInfo.put(p, itemInfo);
        this.claim.put(p, claim);
        p.openInventory(inv);
        return inv;
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (itemInfo.containsKey(p) && this.claim.containsKey(p)) {
            CLAIM_SETTINGS setting = (CLAIM_SETTINGS) itemInfo.get(p).get(e.getSlot()).type_info;
            Claim claim = this.claim.get(p);
            switch (setting) {
                case MEMBERS: PueblosInventory.MEMBER.open(p, claim); break;
                default: p.sendMessage("Not Yet Supported!");
            }
            this.itemInfo.remove(p);
        }
    }

}
