package me.RonanCraft.BetterClaims.player.events;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.claims.ClaimData;
import me.RonanCraft.BetterClaims.claims.Claim;
import me.RonanCraft.BetterClaims.resources.helper.HelperClaim;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface ClaimEvents {

    default BetterClaims getPl() {
        return BetterClaims.getInstance();
    }

    default boolean isProtected(Location loc) {
        return getClaimMain(loc) != null;
    }

    default void sendNotAllowedMsg(Player p, Claim claim) {
        p.sendMessage("You are not allowed to do this here! If you believe you should, send a request to join this claim!");
    }

    default ClaimData getClaimAt(Location loc, boolean ignoreChild) {
        return HelperClaim.getHandler().getClaimAt(loc, ignoreChild);
    }

    default Claim getClaimMain(Location loc) {
        return HelperClaim.getHandler().getClaimMain(loc);
    }

    default boolean allowBreak(Player p, Location block_location) {
        return HelperClaim.getHandler().allowBreak(p, block_location);
    }

    default boolean allowInteract(Player p, Block block) {
        return HelperClaim.getHandler().allowInteract(p, block);
    }

}
