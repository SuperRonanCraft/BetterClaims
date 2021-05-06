package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_ERRORS;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_MODE;
import me.RonanCraft.Pueblos.resources.claims.ClaimMain;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
import me.RonanCraft.Pueblos.resources.tools.visual.Visualization;
import me.RonanCraft.Pueblos.resources.tools.visual.VisualizationType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerClaimInteraction {

    private final Player player;
    final List<Location> locations = new ArrayList<>();
    boolean locked = false;
    public CLAIM_MODE mode;
    public ClaimMain editing;

    PlayerClaimInteraction(Player player, CLAIM_MODE mode) {
        this.player = player;
        this.mode = mode;
    }

    CLAIM_ERRORS addLocation(Player p, Location loc) {
        if (locations.contains(loc)) {
            return CLAIM_ERRORS.LOCATION_ALREADY_EXISTS;
        } else {
            for (ClaimMain claim : Pueblos.getInstance().getClaimHandler().getClaimsMain())
                if (claim.contains(loc)) {
                    if (editing == null && locations.size() == 0 && claim.getBoundingBox().isCorner(loc)) { //Clicked a corner (first)
                        if (claim.isOwner(p) || (claim.isAdminClaim() && PermissionNodes.ADMIN_CLAIM.check(p))) {
                            mode = CLAIM_MODE.EDIT;
                            editing = claim;
                            //Show the claim we are editing
                            Visualization.fromClaim(claim, player.getLocation().getBlockY(), VisualizationType.EDIT, player.getLocation()).apply(player);
                            break;
                        } //else {
                            //Clicked a corner, but not allowed to resize this claim, next method will handle this
                        //}
                    }
                    if (mode != CLAIM_MODE.EDIT || editing != claim) { //Not editing the current overlapping claim area
                        if (editing == null && !claim.isAdminClaim() && (claim.isOwner(p) || Pueblos.getInstance().getPlayerData(p).isOverriding())) {
                            mode = CLAIM_MODE.SUBCLAIM;
                            editing = claim;
                            break;
                        } else {
                            Visualization.fromClaim(claim, player.getLocation().getBlockY(), VisualizationType.ERROR, player.getLocation()).apply(player);
                            return CLAIM_ERRORS.OVERLAPPING;
                        }
                    }
                }
            if (locations.size() > 1 && !Objects.equals(locations.get(0).getWorld(), loc.getWorld())) {
                lock();
                return CLAIM_ERRORS.CANCELLED; //Another world was selected?
            }
            locations.add(loc);
        }
        if (locations.size() > 2)
            locations.remove(1);
        if (locations.size() == 1) { //First Location
            switch (mode) {
                case CREATE: MessagesCore.CLAIM_ITEM_WAND_NORMAL.send(p); break;
                case CREATE_ADMIN: MessagesCore.CLAIM_ITEM_WAND_ADMIN.send(p); break;
                case SUBCLAIM: MessagesCore.CLAIM_ITEM_WAND_SUBCLAIM.send(p); break;
                case EDIT: MessagesCore.CLAIM_ITEM_WAND_EDIT.send(p); break;
            }
        }
        return CLAIM_ERRORS.NONE;
    }

    void lock() {
        locked = true;
    }

    public void reset() {
        locked = false;
        locations.clear();
        if (mode != CLAIM_MODE.CREATE_ADMIN)
            mode = CLAIM_MODE.CREATE;
    }

}
