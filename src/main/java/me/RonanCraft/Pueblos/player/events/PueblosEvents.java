package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface PueblosEvents {

    default Pueblos getPl() {
        return Pueblos.getInstance();
    }

    default boolean isProtected(Location loc) {
        return getClaim(loc) != null;
    }

    default void sendNotAllowedMsg(Player p, Claim claim) {
        p.sendMessage("You are not allowed to do this here! If you believe you should, send a request to join this claim!");
    }

    default Claim getClaim(Location loc) {
        return getPl().getSystems().getClaimHandler().getClaim(loc);
    }

    default boolean allowBreak(Player p, Location block_location) {
        return allowBreak(p, block_location, null);
    }

    default boolean allowBreak(Player p, Location block_location, Block block) {
        return getPl().getSystems().getClaimHandler().allowBreak(p, block_location, block);
    }

    default boolean allowInteract(Player p, Block block) {
        return getPl().getSystems().getClaimHandler().allowInteract(p, block);
    }

}
