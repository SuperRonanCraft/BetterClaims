package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.inventory.confirmation.Confirmation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface PueblosInv_Confirming extends PueblosInv {

    Inventory open(Player p, Confirmation confirmation);

}
