package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
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

import java.util.HashMap;
import java.util.List;

public class EventInteract {

    private final EventListener listener;
    private final HashMap<Player, Integer> cancelTimers = new HashMap<>();

    EventInteract(EventListener listener) {
        this.listener = listener;
    }

    //Player Interact
    void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null || e.isCancelled())
            return;
        Block block = e.getClickedBlock();
        Claim claim = listener.getClaim(block.getLocation());
        if (claim != null && !claim.isOwner(e.getPlayer())) {
            ClaimMember member = claim.getMember(e.getPlayer());
            if (member == null) {
                e.setCancelled(true);
                return;
            }
            CLAIM_FLAG flag = null;
            if (block.getType().name().contains("LEVER")) {
                flag = CLAIM_FLAG.ALLOW_LEVER;
            } else if (block.getType().name().contains("DOOR")) {
                flag = CLAIM_FLAG.ALLOW_DOOR;
            } else if (block.getType().name().contains("BUTTON")) {
                flag = CLAIM_FLAG.ALLOW_BUTTON;
            } else if (block.getType().name().contains("BED")) {
                flag = CLAIM_FLAG.ALLOW_BED;
            }
            if (flag != null) {
                //Check member flag value (if it exists)
                CLAIM_FLAG_MEMBER memberFlag = flag.getMemberEquivalent();
                Object flagValue = claim.getFlags().getFlag(flag); //Get the claims flag value
                if (memberFlag != null)
                    flagValue = member.getFlags().getOrDefault(memberFlag, memberFlag.getDefault()); //Get the members flag value
                e.setCancelled(!(Boolean) flagValue); //Are they allowed to do this here?
            } else {
                CLAIM_FLAG_MEMBER memberFlag = null;
                if (block.getType().name().contains("CHEST"))
                    memberFlag = CLAIM_FLAG_MEMBER.ALLOW_CHEST;
                Object flagValue = null;
                if (memberFlag != null)
                    flagValue = member.getFlags().getOrDefault(memberFlag, memberFlag.getDefault());
                if (flagValue != null)
                    e.setCancelled(!(Boolean) flagValue); //Are they allowed to do this here?
            }
        }
    }

    void onInteractCreateClaim(PlayerInteractEvent e) {
        if (    e.getItem() == null
                || (e.isCancelled() && e.getAction() != Action.RIGHT_CLICK_AIR)
                || (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR)
                || !e.getItem().getType().equals(Material.GOLDEN_SHOVEL))
            return;

        Block block = e.getClickedBlock();
        if (block == null || block.getType() == Material.AIR) //Block aiming at distance
            block = e.getPlayer().getTargetBlock(null, 32);
        if (block.getType() == Material.AIR)
            return;
        Location loc = block.getLocation();

        e.setCancelled(true);
        Player p = e.getPlayer();
        if (!listener.claimInteraction.containsKey(p))
            listener.claimInteraction.put(p, new PlayerClaimInteraction(p, PlayerClaimInteraction.CLAIM_MODE.CREATE));
        PlayerClaimInteraction claimInteraction = listener.claimInteraction.get(p);
        //Can we create a claim?
        if (!claimInteraction.locked) {
            cancelInteraction(p, 60L);
            CLAIM_ERRORS error = claimInteraction.addLocation(p, loc);
            if (error == CLAIM_ERRORS.NONE) {
                List<Location> corners = claimInteraction.locations;
                if (corners.size() >= 2) { //Create claim
                    if (claimInteraction.mode == PlayerClaimInteraction.CLAIM_MODE.CREATE) {
                        error = HelperClaim.createClaim(p, corners.get(0), corners.get(1));
                    } else if (claimInteraction.mode == PlayerClaimInteraction.CLAIM_MODE.EDIT) { //Edit claim size mode
                        error = resizeClaim(p, claimInteraction.editing, corners);
                    }
                    if (error != CLAIM_ERRORS.SIZE) //Let the player select another second location
                        claimInteraction.lock(); //Lock us from using the same locations again
                } else {
                    Visualization.fromLocation(loc, p.getLocation().getBlockY(), p.getLocation()).apply(p);
                }
                if (claimInteraction.locked)
                    cancelInteraction(p, 2L);
            } else if (error == CLAIM_ERRORS.LOCATION_ALREADY_EXISTS) {
                Visualization.fromLocation(loc, p.getLocation().getBlockY(), p.getLocation()).apply(p);
            } else if (error == CLAIM_ERRORS.OVERLAPPING) {
                claimInteraction.lock();
                cancelInteraction(p, 2L);
            }
            error.sendMsg(p, null);
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
        CLAIM_ERRORS error = Pueblos.getInstance().getSystems().getClaimHandler().isLocationValid(greater, lower, p, claim /*Ignored claim*/);
        if (error == CLAIM_ERRORS.NONE) {
            //Save position
            claim.editCorners(positionStiff, positionMovingCorner);
            MessagesCore.CLAIM_RESIZED.send(p, claim);
            Visualization.fromClaim(claim, p.getLocation().getBlockY(), VisualizationType.CLAIM, p.getLocation()).apply(p);
        } else
            error.sendMsg(p, claim);
        return error;
    }

    private void cancelInteraction(Player p, Long time) {
        if (cancelTimers.containsKey(p))
            Bukkit.getScheduler().cancelTask(cancelTimers.get(p));
        cancelTimers.put(p, Bukkit.getScheduler().scheduleSyncDelayedTask(Pueblos.getInstance(), () -> {
            listener.claimInteraction.remove(p);
        }, time * 20L));
    }
}
