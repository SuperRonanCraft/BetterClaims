package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.claims.ClaimMain;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface PueblosInv_Claim extends PueblosInv {

    Inventory open(Player p, ClaimMain claim);

}
