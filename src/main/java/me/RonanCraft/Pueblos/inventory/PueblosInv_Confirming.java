package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.tools.CONFIRMATION_TYPE;
import me.RonanCraft.Pueblos.resources.tools.Confirmation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface PueblosInv_Confirming extends PueblosInv {

    Inventory open(Player p, Confirmation confirmation);

}
