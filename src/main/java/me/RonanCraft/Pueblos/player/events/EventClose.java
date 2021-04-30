package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.player.data.PlayerData;
import me.RonanCraft.Pueblos.player.data.PlayerInfoHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class EventClose {

    void exit(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        Pueblos pl = Pueblos.getInstance();
        PlayerData data = pl.getSystems().getPlayerData(p);
        if (data.getInventory() != null && e.getInventory().equals(data.getInventory())) {
            if (data.getCurrent() != null) {
                data.getCurrent().closeEvent(p); //Close Inventory event
                data.clearInventory();
            }
        } else if (data.getInventory() != null) { //Inventory was lost to something else
            data.clearInventory();
        }
    }

}
