package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface PueblosInv_Member extends PueblosInv {

    default Pueblos getPl() {
        return Pueblos.getInstance();
    }

    Inventory open(Player p, ClaimMember member);

}
