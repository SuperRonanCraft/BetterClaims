package me.RonanCraft.BetterClaims.inventory;

import me.RonanCraft.BetterClaims.claims.ClaimData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public interface ClaimInv_MultiClaim extends ClaimInv {

    Inventory open(Player p, List<ClaimData> claimDataList);

}
