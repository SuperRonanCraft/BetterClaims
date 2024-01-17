package me.RonanCraft.BetterClaims.player.events;

import me.RonanCraft.BetterClaims.player.data.PlayerData;
import me.RonanCraft.BetterClaims.resources.helper.HelperPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class EventClose {

    void exit(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        PlayerData data = HelperPlayer.getData(p);
        if (data.getInventory() != null && e.getInventory().equals(data.getInventory())) {
            if (data.getClaimInventory() != null) {
                data.getClaimInventory().closeEvent(p); //Close Inventory event
                data.clearInventory();
            }
        } else if (data.getInventory() != null) { //Inventory was lost to something else
            data.clearInventory();
        }
    }

}
