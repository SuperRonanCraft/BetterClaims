package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.player.data.PlayerData;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.Settings;
import me.RonanCraft.Pueblos.resources.claims.*;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
import me.RonanCraft.Pueblos.resources.tools.HelperClaim;
import me.RonanCraft.Pueblos.resources.tools.visual.Visualization;
import me.RonanCraft.Pueblos.resources.tools.visual.VisualizationType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.List;

public class EventInteract implements PueblosEvents {

    private final EventListener listener;
    private final HashMap<Player, Integer> cancelTimers = new HashMap<>();
    Material claim_item;

    EventInteract(EventListener listener) {
        this.listener = listener;
    }

    void load() {
        String item = Pueblos.getInstance().getSettings().getString(Settings.SETTING.CLAIM_ITEM);
        try {
            claim_item = Material.valueOf(item.toUpperCase());
        } catch (IllegalArgumentException e) {
            Pueblos.getInstance().getLogger().severe("The item `" + item + "` is not a valid item, changed back to golden_shovel! " +
                    "Please change the claim `Item` in the config!");
            claim_item = Material.GOLDEN_SHOVEL;
        }
    }

    //Player Interact with Button, Levers, Beds, Doors, Chests (for members)...
    void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null || e.isCancelled())
            return;
        if (!allowInteract(e.getPlayer(), e.getClickedBlock()))
            e.setCancelled(true);
    }

    //Create a claim
    void onInteractCreateClaim(PlayerInteractEvent e) {
        if (    e.getItem() == null
                || (e.isCancelled() && e.getAction() != Action.RIGHT_CLICK_AIR)
                || (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR)
                || !e.getItem().getType().equals(claim_item))
            return;

        Block block = e.getClickedBlock();
        if (block == null || block.getType() == Material.AIR) //Block aiming at distance
            block = e.getPlayer().getTargetBlock(null, 64);
        if (block.getType() == Material.AIR)
            return;
        Location loc = block.getLocation();

        e.setCancelled(true);
        Player p = e.getPlayer();
        PlayerData data = listener.getPlayerData(p);
        if (data.getClaimInteraction() == null)
            data.setClaimInteraction(new PlayerClaimInteraction(p, PlayerClaimInteraction.CLAIM_MODE.CREATE));
        PlayerClaimInteraction claimInteraction = data.getClaimInteraction();
        if (!claimInteraction.locked) { //Are we NOT locked from doing anything?
            Object errorInfo = null;
            resetInteraction(p, 60L);
            CLAIM_ERRORS error = claimInteraction.addLocation(p, loc);
            if (error == CLAIM_ERRORS.NONE) {
                List<Location> corners = claimInteraction.locations;
                if (corners.size() >= 2) { //Create claim
                    switch (claimInteraction.mode) {
                        //Normal user claim
                        case CREATE:
                        //Create a claim with no owner, making it an admin claim
                        case CREATE_ADMIN:
                            error = HelperClaim.createClaim(p, p.getWorld(), corners.get(0), corners.get(1), false, claimInteraction.mode); break; //MODE will handle the rest
                        //Edit a claims size
                        case EDIT: error = resizeClaim(p, claimInteraction.editing, corners); errorInfo = claimInteraction.editing; break;
                        //Create a claim inside another claim
                        case SUBCLAIM: break;
                    }
                    if (error != CLAIM_ERRORS.SIZE_SMALL && error != CLAIM_ERRORS.SIZE_LARGE) //Let the player select another second location if error
                        claimInteraction.lock(); //Lock us from using the same locations again
                } else if (claimInteraction.mode != PlayerClaimInteraction.CLAIM_MODE.EDIT) { //If the player is editing the claim they have selected will visualize
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
    }

    private CLAIM_ERRORS resizeClaim(Player p, Claim claim, List<Location> corners) {

        ClaimPosition position = claim.getPosition();
        Location start_location = corners.get(0);

        ClaimPosition.CLAIM_CORNER edittedCorner = position.getCorner(start_location);
        if (edittedCorner == null) {
            p.sendMessage("We could not find the stiff corner!");
            return CLAIM_ERRORS.DATABASE_ERROR;
        }
        assert edittedCorner.opposite() != null;
        Location positionStiff = position.getCorner(edittedCorner.opposite()); //Location that wont move, due to editing the opposite corner
        Location positionMovingCorner = corners.get(1);
        int max_x = Math.max(positionStiff.getBlockX(), positionMovingCorner.getBlockX());
        int max_z = Math.max(positionStiff.getBlockZ(), positionMovingCorner.getBlockZ());
        int min_x = Math.min(positionStiff.getBlockX(), positionMovingCorner.getBlockX());
        int min_z = Math.min(positionStiff.getBlockZ(), positionMovingCorner.getBlockZ());
        Location greater = new Location(position.getWorld(), max_x, 0, max_z);
        Location lower = new Location(position.getWorld(), min_x, 0, min_z);
        CLAIM_ERRORS error = Pueblos.getInstance().getClaimHandler().isLocationValid(greater, lower, p, claim /*Ignored claim*/);
        if (error == CLAIM_ERRORS.NONE) {
            //Save new position
            if (claim.editCorners(p, positionStiff, positionMovingCorner)) {
                MessagesCore.CLAIM_RESIZED.send(p, claim);
                Visualization.fromClaim(claim, p.getLocation().getBlockY(), VisualizationType.CLAIM, p.getLocation()).apply(p);
            }
        }// else
         //   error.sendMsg(p, claim);
        return error;
    }

    //Reset all locations and reset mode if editing
    private void resetInteraction(Player p, Long time) {
        if (cancelTimers.containsKey(p))
            Bukkit.getScheduler().cancelTask(cancelTimers.get(p));
        cancelTimers.put(p, Bukkit.getScheduler().scheduleSyncDelayedTask(Pueblos.getInstance(), () -> {
            if (listener.getPlayerData(p).getClaimInteraction() != null)
                listener.getPlayerData(p).getClaimInteraction().reset();
        }, time * 20L));
    }
}
