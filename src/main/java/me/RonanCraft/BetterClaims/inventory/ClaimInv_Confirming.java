package me.RonanCraft.BetterClaims.inventory;

import me.RonanCraft.BetterClaims.inventory.confirmation.Confirmation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface ClaimInv_Confirming extends ClaimInv {

    Inventory open(Player p, Confirmation confirmation);

}
