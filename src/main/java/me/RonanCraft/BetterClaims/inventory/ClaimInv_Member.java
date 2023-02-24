package me.RonanCraft.BetterClaims.inventory;

import me.RonanCraft.BetterClaims.claims.data.members.Member;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface ClaimInv_Member extends ClaimInv {

    Inventory open(Player p, Member member);

}
