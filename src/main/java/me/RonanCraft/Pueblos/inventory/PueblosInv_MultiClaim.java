package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public interface PueblosInv_MultiClaim extends PueblosInv {

    Inventory open(Player p, List<Claim> claimList);

}
