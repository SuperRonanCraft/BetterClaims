package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.claims.data.members.Member;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface PueblosInv_Member extends PueblosInv {

    Inventory open(Player p, Member member);

}
