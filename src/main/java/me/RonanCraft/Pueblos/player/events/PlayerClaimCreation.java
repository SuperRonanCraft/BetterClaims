package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.CLAIM_ERRORS;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.tools.visual.Visualization;
import me.RonanCraft.Pueblos.resources.tools.visual.VisualizationType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerClaimCreation {

    private final Player player;
    final List<Location> locations = new ArrayList<>();
    boolean locked = false;

    PlayerClaimCreation(Player player) {
        this.player = player;
    }

    CLAIM_ERRORS addLocation(Location loc) {
        if (locations.contains(loc)) {
            return CLAIM_ERRORS.LOCATION_ALREADY_EXISTS;
        } else {
            for (Claim claim : Pueblos.getInstance().getSystems().getClaimHandler().getClaims())
                if (claim.contains(loc)) {
                    Visualization.fromClaim(claim, player.getLocation().getBlockY(), VisualizationType.ERROR, player.getLocation()).apply(player);
                    return CLAIM_ERRORS.OVERLAPPING;
                }
            locations.add(loc);
        }
        if (locations.size() > 2)
            locations.remove(1);
        return CLAIM_ERRORS.NONE;
    }

    void lock() {
        locked = true;
    }
}
