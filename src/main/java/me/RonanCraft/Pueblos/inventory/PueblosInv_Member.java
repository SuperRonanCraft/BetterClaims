package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface PueblosInv_Member extends PueblosInv {

    Inventory open(Player p, ClaimMember member);

}
