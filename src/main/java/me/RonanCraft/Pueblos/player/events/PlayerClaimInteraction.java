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

public class PlayerClaimInteraction {

    private final Player player;
    final List<Location> locations = new ArrayList<>();
    boolean locked = false;
    CLAIM_MODE mode;
    Claim editing;

    PlayerClaimInteraction(Player player, CLAIM_MODE mode) {
        this.player = player;
        this.mode = mode;
    }

    CLAIM_ERRORS addLocation(Player p, Location loc) {
        if (locations.contains(loc)) {
            return CLAIM_ERRORS.LOCATION_ALREADY_EXISTS;
        } else {
            for (Claim claim : Pueblos.getInstance().getSystems().getClaimHandler().getClaims())
                if (claim.contains(loc)) {
                    if (locations.size() == 0 && claim.getPosition().isCorner(loc)) { //Clicked a corner (first)
                        if (claim.isOwner(p)) {
                            mode = CLAIM_MODE.EDIT;
                            editing = claim;
                            break;
                        } else {
                            //Clicked a corner, but not allowed to resize this claim
                        }
                    }
                    if (!(mode == CLAIM_MODE.EDIT && editing == claim)) {
                        Visualization.fromClaim(claim, player.getLocation().getBlockY(), VisualizationType.ERROR, player.getLocation()).apply(player);
                        return CLAIM_ERRORS.OVERLAPPING;
                    }
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

    public void reset() {
        locked = false;
        locations.clear();
        if (mode == CLAIM_MODE.EDIT)
            mode = CLAIM_MODE.CREATE;
    }

    public enum CLAIM_MODE {
        EDIT,
        CREATE,
        CREATE_ADMIN,
        SUBCLAIM
    }
}
