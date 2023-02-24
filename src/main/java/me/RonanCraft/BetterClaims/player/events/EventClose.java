package me.RonanCraft.BetterClaims.player.events;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.player.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class EventClose {

    void exit(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        BetterClaims pl = BetterClaims.getInstance();
        PlayerData data = pl.getPlayerData(p);
        if (data.getInventory() != null && e.getInventory().equals(data.getInventory())) {
            if (data.getCurrentInventory() != null) {
                data.getCurrentInventory().closeEvent(p); //Close Inventory event
                data.clearInventory();
            }
        } else if (data.getInventory() != null) { //Inventory was lost to something else
            data.clearInventory();
        }
    }

}
