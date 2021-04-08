package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClaimMembers implements PueblosInv_ClaimInfo {

    Player p;

    @Override
    public Inventory open(Player p, Claim claim) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, "Members");

        //Decoration
        for (int i = 0; i < inv.getSize(); i++) {
            if (i < 9 || i > inv.getSize() - 9 || i % 9 == 0 || i % 9 - 8 == 0)
                inv.setItem(i, new ItemStack(Material.CYAN_STAINED_GLASS_PANE));
        }

        for (ClaimMember member : claim.getMembers()) {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            setTitle(item, member.getPlayer(), "&7" + member.name);
            inv.setItem(inv.firstEmpty(), item);
        }
        p.openInventory(inv);
        return inv;
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {

    }

}
