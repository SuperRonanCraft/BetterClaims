package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.claims.ClaimMain;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public interface PueblosInv_MultiClaim extends PueblosInv {

    Inventory open(Player p, List<ClaimMain> claimList);

}
