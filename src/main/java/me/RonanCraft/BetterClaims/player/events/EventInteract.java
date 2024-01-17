package me.RonanCraft.BetterClaims.player.events;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.claims.data.BoundingBox;
import me.RonanCraft.BetterClaims.player.data.PlayerData;
import me.RonanCraft.BetterClaims.resources.Settings;
import me.RonanCraft.BetterClaims.claims.*;
import me.RonanCraft.BetterClaims.claims.enums.*;
import me.RonanCraft.BetterClaims.resources.messages.MessagesCore;
import me.RonanCraft.BetterClaims.resources.helper.HelperClaim;
import me.RonanCraft.BetterClaims.resources.visualization.Visualization;
import me.RonanCraft.BetterClaims.resources.visualization.VisualizationType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.*;

public class EventInteract implements ClaimEvents {

    private final EventListener listener;
    private final HashMap<Player, Integer> cancelTimers = new HashMap<>();
    Material claim_item;

    EventInteract(EventListener listener) {
        this.listener = listener;
    }

    void load() {
        String item = BetterClaims.getInstance().getSettings().getString(Settings.SETTING.CLAIM_ITEM);
        try {
            claim_item = Material.valueOf(item.toUpperCase());
        } catch (IllegalArgumentException e) {
            BetterClaims.getInstance().getLogger().severe("The item `" + item + "` is not a valid item, changed back to a golden shovel! " +
                    "Please change the claim `Item` in the config!");
            claim_item = null;
            try {
                claim_item = Material.valueOf("GOLD_SPADE");//.GOLD_SPADE;
            } catch (IllegalArgumentException ec) {
                claim_item = Material.valueOf("GOLDEN_SHOVEL");
            }
        }
    }

    //Player Interact with Button, Levers, Beds, Doors, Chests (for members)...
    void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null || e.isCancelled())
            return;
        if (!allowInteract(e.getPlayer(), e.getClickedBlock()))
            e.setCancelled(true);
        if (e.getItem() != null && e.getItem().getType() == Material.STICK) {
            HashMap<CLAIM_FLAG, Object> flags = HelperClaim.getHandler().getFlagsAt(e.getClickedBlock().getLocation(), false);
        }
    }

    //Create a claim
    void onInteractCreateClaim(PlayerInteractEvent e) {
        if (    e.getItem() == null
                || (e.isCancelled() && e.getAction() != Action.RIGHT_CLICK_AIR)
                || (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR)
                || !e.getItem().getType().equals(claim_item)
                || !HelperClaim.worldEnabled(e.getPlayer().getWorld()))
            return;

        Bukkit.getScheduler().runTaskAsynchronously(BetterClaims.getInstance(), () -> {
            Block block = e.getClickedBlock();
            if (block == null || block.getType() == Material.AIR) { //Block aiming at distance
                try {
                    block = e.getPlayer().getTargetBlock((Set<Material>) null, 64);
                } catch (IllegalStateException ignored) {
                    return;
                }
            }
            if (block.getType() == Material.AIR)
                return;
            Location loc = block.getLocation();

            e.setCancelled(true);
            Player p = e.getPlayer();
            PlayerData data = listener.getPlayerData(p);
            if (data.getClaimInteraction() == null)
                data.setClaimInteraction(new PlayerClaimInteraction(p, CLAIM_MODE.CREATE));
            PlayerClaimInteraction claimInteraction = data.getClaimInteraction();
            if (!claimInteraction.locked) { //Are we NOT locked from doing anything?
                Object errorInfo = null;
                resetInteraction(p, 60L);
                CLAIM_ERRORS error = claimInteraction.addLocation(p, loc);
                if (error == CLAIM_ERRORS.NONE) {
                    List<Location> corners = claimInteraction.locations;
                    if (corners.size() >= 2) { //Create claim
                        switch (claimInteraction.mode) {
                            case CREATE:
                                //Normal user claim
                            case CREATE_ADMIN:
                                //Create a claim with no owner
                            case SUBCLAIM:
                                //Create a claim inside another claim
                                error = HelperClaim.registerClaim(p, p.getWorld(), corners.get(0), corners.get(1),
                                        claimInteraction,
                                        claimInteraction.mode == CLAIM_MODE.SUBCLAIM ? CLAIM_TYPE.CHILD : CLAIM_TYPE.PARENT);
                                break; //MODE will handle the rest
                            case EDIT:
                                //Edits a claims size
                                error = resizeClaim(p, claimInteraction); errorInfo = claimInteraction.editing;
                                break;
                        }
                        if (error != CLAIM_ERRORS.SIZE_SMALL && error != CLAIM_ERRORS.SIZE_LARGE) //Let the player select another second location if error
                            claimInteraction.lock(); //Lock us from using the same locations again
                    } else if (claimInteraction.mode != CLAIM_MODE.EDIT) { //If the player is editing the claim they have selected will visualize
                        if (claimInteraction.mode == CLAIM_MODE.SUBCLAIM) {
                            Visualization vis = Visualization.fromClaim(claimInteraction.editing, p.getLocation().getBlockY(), VisualizationType.CLAIM, p.getLocation());
                            Visualization.fromLocation(vis, loc, p.getLocation().getBlockY(), p.getLocation()).apply(p);
                        } else
                            Visualization.fromLocation(loc, p.getLocation().getBlockY(), p.getLocation()).apply(p);
                    }
                    if (claimInteraction.locked)
                        resetInteraction(p, 2L);
                } else if (error == CLAIM_ERRORS.LOCATION_ALREADY_EXISTS) {
                    Visualization.fromLocation(loc, p.getLocation().getBlockY(), p.getLocation()).apply(p);
                } else if (error == CLAIM_ERRORS.OVERLAPPING) {
                    claimInteraction.lock();
                    resetInteraction(p, 2L);
                }
                error.sendMsg(p, errorInfo);
            }
        });
    }

    private CLAIM_ERRORS resizeClaim(Player p, PlayerClaimInteraction claimInteraction) {

        BoundingBox position = claimInteraction.editing.getBoundingBox();
        Vector start_location = claimInteraction.locations.get(0).toVector();

        CLAIM_CORNER edittedCorner = position.getCorner(start_location);
        if (edittedCorner == null) {
            p.sendMessage("We could not find the stiff corner!");
            return CLAIM_ERRORS.DATABASE_ERROR;
        }
        assert edittedCorner.opposite() != null;
        Vector positionStiff = position.getCorner(edittedCorner.opposite()); //Location that wont move, due to editing the opposite corner
        Vector positionMovingCorner = claimInteraction.locations.get(1).toVector();
        int max_x = Math.max(positionStiff.getBlockX(), positionMovingCorner.getBlockX());
        int max_z = Math.max(positionStiff.getBlockZ(), positionMovingCorner.getBlockZ());
        int min_x = Math.min(positionStiff.getBlockX(), positionMovingCorner.getBlockX());
        int min_z = Math.min(positionStiff.getBlockZ(), positionMovingCorner.getBlockZ());
        Location greater = new Location(claimInteraction.editing.getWorld(), max_x, 0, max_z);
        Location lower = new Location(claimInteraction.editing.getWorld(), min_x, 0, min_z);
        List<ClaimData> ignoredClaimData = new ArrayList<>();
        ignoredClaimData.add(claimInteraction.editing);
        if (claimInteraction.editing instanceof Claim_Child)
            ignoredClaimData.add(((Claim_Child) claimInteraction.editing).getParent());
        CLAIM_ERRORS error = HelperClaim.getHandler().isLocationValid(greater, lower, p, ignoredClaimData /*Ignored claim*/, claimInteraction);
        if (error == CLAIM_ERRORS.NONE) {
            //Save new position
            error = claimInteraction.editing.editCorners(p, positionStiff, positionMovingCorner);
            if (error == CLAIM_ERRORS.NONE) {
                MessagesCore.CLAIM_RESIZE_SUCCESS.send(p, claimInteraction.editing);
                Visualization.fromClaim(claimInteraction.editing, p.getLocation().getBlockY(), VisualizationType.CLAIM, p.getLocation()).apply(p);
            }
        }// else
         //   error.sendMsg(p, claim);
        return error;
    }

    //Reset all locations and reset mode if editing
    private void resetInteraction(Player p, Long time) {
        if (cancelTimers.containsKey(p))
            Bukkit.getScheduler().cancelTask(cancelTimers.get(p));
        cancelTimers.put(p, Bukkit.getScheduler().scheduleSyncDelayedTask(BetterClaims.getInstance(), () -> {
            if (listener.getPlayerData(p).getClaimInteraction() != null)
                Objects.requireNonNull(listener.getPlayerData(p).getClaimInteraction()).reset();
        }, time * 20L));
    }
}
