package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryClaimMembers implements PueblosInv_Claim {

    private final HashMap<Player, HashMap<Integer, PueblosItem>> map = new HashMap<>();

    @Override
    public Inventory open(Player p, Claim claim) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, "Members " + claim.getName());

        addBorder(inv);

        HashMap<Integer, PueblosItem> itemSlots = new HashMap<>();
        for (ClaimMember member : claim.getMembers()) {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            setTitle(item, member.getPlayer(), "&7" + member.name);
            List<String> lore = new ArrayList<>();
            lore.add("Owner: " + member.owner);
            if (!member.flags.isEmpty()) {
                lore.add("Flags");
                member.flags.forEach((flag, value) -> {
                    lore.add(" - " + flag.name() + ": " + value.toString());
                });
            }
            setLore(item, p, lore);
            int slot = inv.firstEmpty();
            inv.setItem(slot, item);
            itemSlots.put(slot, new PueblosItem(item, member));
        }
        map.put(p, itemSlots);
        p.openInventory(inv);
        return inv;
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (map.containsKey(p)) {
            ClaimMember member = (ClaimMember) map.get(p).get(e.getSlot()).type_info;
            PueblosInventory.MEMBER.open(p, member);
            map.remove(p);
        }
    }

}
