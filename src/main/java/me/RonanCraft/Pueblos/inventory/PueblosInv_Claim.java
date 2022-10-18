package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.claims.ClaimData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface PueblosInv_Claim extends PueblosInv {

    Inventory open(Player p, ClaimData claimData);

}
