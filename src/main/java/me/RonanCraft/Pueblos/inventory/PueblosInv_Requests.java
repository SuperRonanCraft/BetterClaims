package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public interface PueblosInv_Requests extends PueblosInv {

    default Pueblos getPl() {
        return Pueblos.getInstance();
    }

    Inventory open(Player p, List<Claim> claimList);

}
