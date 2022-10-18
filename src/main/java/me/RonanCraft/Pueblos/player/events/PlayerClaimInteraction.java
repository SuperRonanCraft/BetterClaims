package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.claims.ClaimData;
import me.RonanCraft.Pueblos.claims.Claim_Child;
import me.RonanCraft.Pueblos.claims.enums.CLAIM_ERRORS;
import me.RonanCraft.Pueblos.claims.enums.CLAIM_MODE;
import me.RonanCraft.Pueblos.claims.Claim;
import me.RonanCraft.Pueblos.resources.messages.MessagesCore;
import me.RonanCraft.Pueblos.resources.visualization.Visualization;
import me.RonanCraft.Pueblos.resources.visualization.VisualizationType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerClaimInteraction {

    private final Player player;
    final List<Location> locations = new ArrayList<>();
    boolean locked = false;
    public CLAIM_MODE mode;
    public ClaimData editing;

    PlayerClaimInteraction(Player player, CLAIM_MODE mode) {
        this.player = player;
        this.mode = mode;
    }

    CLAIM_ERRORS addLocation(Player p, Location loc) {
        if (locations.contains(loc)) {
            return CLAIM_ERRORS.LOCATION_ALREADY_EXISTS;
        } else {
            for (Claim claim : Pueblos.getInstance().getClaimHandler().getClaimsMain())
                if (claim.contains(loc)) {
                    if (editing == null && locations.size() == 0 && (claim.isOwner(p) || (claim.isAdminClaim() && PermissionNodes.ADMIN_CLAIM.check(p)))) {
                        if (claim.getBoundingBox().isCorner(loc)) { //Clicked a corner (first)
                            mode = CLAIM_MODE.EDIT;
                            //Show the claim we are editing
                            Visualization vis = Visualization.fromClaim(claim, player.getLocation().getBlockY(), VisualizationType.EDIT, player.getLocation());
                            Visualization.fromLocation(vis, loc, player.getLocation().getBlockY(), p.getLocation()).apply(player);
                        } else {
                            List<Claim_Child> children = Pueblos.getInstance().getClaimHandler().getClaimsChild(claim);
                            for (Claim_Child child : children)
                                if (child.getBoundingBox().isCorner(loc)) {
                                    mode = CLAIM_MODE.EDIT;
                                    editing = child;
                                    Visualization vis = Visualization.fromClaim(claim, player.getLocation().getBlockY(), VisualizationType.EDIT, player.getLocation());
                                    Visualization.fromLocation(vis, loc, player.getLocation().getBlockY(), p.getLocation()).apply(player);
                                    break;
                                }
                            if (editing == null)
                                mode = CLAIM_MODE.SUBCLAIM;
                        }
                        if (editing == null)
                            editing = claim;
                        break;
                    }
                    if (editing == null || (editing != claim && (!(editing instanceof Claim_Child) || ((Claim_Child) editing).getParent() != claim))) { //Not editing the current overlapping claim area
                        Visualization.fromClaim(claim, player.getLocation().getBlockY(), VisualizationType.ERROR, player.getLocation()).apply(player);
                        //player.sendMessage("OVERLAPPING!");
                        return CLAIM_ERRORS.OVERLAPPING;
                    }
                }
            if (!locations.isEmpty() && !Objects.equals(locations.get(0).getWorld(), loc.getWorld())) {
                lock();
                return CLAIM_ERRORS.CANCELLED; //Another world was selected?
            }
            if (mode == CLAIM_MODE.SUBCLAIM)
                if (!locations.isEmpty() && !editing.contains(loc)) { //Disallow child claims to go outside of parent claim
                    Visualization.fromClaim(editing, player.getLocation().getBlockY(), VisualizationType.ERROR, player.getLocation()).apply(player);
                    return CLAIM_ERRORS.OVERLAPPING;
                } else {
                    //Disallow making multiple children claims on top of each other
                    List<Claim_Child> children = Pueblos.getInstance().getClaimHandler().getClaimsChild((Claim) editing);
                    for (Claim_Child child : children)
                        if (child.contains(loc)) {
                            //player.sendMessage("OVERLAPPING2!");
                            return CLAIM_ERRORS.OVERLAPPING;
                        }
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
        editing = null;
        if (mode != CLAIM_MODE.CREATE_ADMIN)
            mode = CLAIM_MODE.CREATE;
    }

}
