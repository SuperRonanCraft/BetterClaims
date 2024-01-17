package me.RonanCraft.BetterClaims.player.events;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.inventory.ClaimInventory;
import me.RonanCraft.BetterClaims.resources.helper.HelperPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class EventClick {

    void click(InventoryClickEvent e) {
        //System.out.println("Move event2");
        if (!validClick(e))
            return;
        e.setCancelled(true);
        ClaimInventory inventory = HelperPlayer.getData((Player) e.getWhoClicked()).getClaimInventory();
        if (inventory != null)
            inventory.click(e);
    }

    private boolean validClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player) || e.isCancelled())
            return false;

        // Clicks the inventory
        if (!e.getInventory().equals(HelperPlayer.getData((Player) e.getWhoClicked()).getInventory()))
            return false;

        // Clicks number key
        if (e.getCurrentItem() == null) {
            if (e.getClick() == ClickType.NUMBER_KEY)
                e.setCancelled(true);
            return false;
        }

        // Checks if item is valid
        if (e.getCurrentItem() == null)
            return false;

        // Clicks their own inventory
        if (!e.getClickedInventory().equals(HelperPlayer.getData((Player) e.getWhoClicked()).getInventory())) {
            e.setCancelled(true);
            return false;
        }
        return true;
    }

}
