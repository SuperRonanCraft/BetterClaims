package me.RonanCraft.BetterClaims.inventory;

import me.RonanCraft.BetterClaims.inventory.confirmation.Confirmation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface PueblosInv_Confirming extends PueblosInv {

    Inventory open(Player p, Confirmation confirmation);

}
