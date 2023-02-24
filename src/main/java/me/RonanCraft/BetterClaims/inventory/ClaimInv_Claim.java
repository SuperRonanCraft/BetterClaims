package me.RonanCraft.BetterClaims.inventory;

import me.RonanCraft.BetterClaims.claims.ClaimData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface ClaimInv_Claim extends ClaimInv {

    Inventory open(Player p, ClaimData claimData);

}
