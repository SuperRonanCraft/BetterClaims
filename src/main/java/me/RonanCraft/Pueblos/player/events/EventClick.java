package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.inventory.PueblosInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class EventClick {

    void click(InventoryClickEvent e) {
        //System.out.println("Move event2");
        if (!validClick(e))
            return;
        e.setCancelled(true);
        PueblosInventory inventory = Pueblos.getInstance().getPlayerData((Player) e.getWhoClicked()).getCurrent();
        if (inventory != null)
            inventory.click(e);
    }

    private boolean validClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player) || e.isCancelled())
            return false;

        // Clicks the inventory
        if (!e.getInventory().equals(Pueblos.getInstance().getPlayerData((Player) e.getWhoClicked()).getInventory()))
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
        if (!e.getClickedInventory().equals(Pueblos.getInstance().getPlayerData((Player) e.getWhoClicked()).getInventory())) {
            e.setCancelled(true);
            return false;
        }
        return true;
    }

}
