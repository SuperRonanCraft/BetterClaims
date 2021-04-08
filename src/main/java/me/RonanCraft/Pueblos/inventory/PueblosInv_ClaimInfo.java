package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface PueblosInv_ClaimInfo extends PueblosInv {

    default Pueblos getPl() {
        return Pueblos.getInstance();
    }

    Inventory open(Player p, Claim claim);

}
