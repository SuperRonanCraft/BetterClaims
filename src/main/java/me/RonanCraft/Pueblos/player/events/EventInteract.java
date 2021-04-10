package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.*;
import me.RonanCraft.Pueblos.resources.files.msgs.Message;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
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
        if (!listener.claimCreation.containsKey(p))
            listener.claimCreation.put(p, new PlayerClaimCreation(p));
        PlayerClaimCreation claimCreation = listener.claimCreation.get(p);
        //Can we create a claim?
        if (!claimCreation.locked) {
            cancelCreation(p, 60L);
            CLAIM_ERRORS error = claimCreation.addLocation(loc);
            if (error == CLAIM_ERRORS.NONE) {
                List<Location> corners = claimCreation.locations;
                if (corners.size() >= 2) {
                    ClaimHandler handler = Pueblos.getInstance().getSystems().getClaimHandler();
                    Claim claim = handler.claimCreate(p.getUniqueId(), p.getName(), new ClaimPosition(p.getWorld(), corners.get(0), corners.get(1)));
                    if (claim != null) {
                        error = handler.addClaim(claim, p);
                        switch (error) {
                            case NONE:
                                MessagesCore.CLAIM_CREATE_SUCCESS.send(p);
                                Visualization.fromClaim(claim, p.getLocation().getBlockY(), VisualizationType.CLAIM, p.getLocation()).apply(p);
                                claimCreation.lock(); //Lock us from making another claim using this item
                                break;
                            case SIZE:
                                MessagesCore.CLAIM_CREATE_FAILED_SIZE.send(p);
                                break;
                            case OVERLAPPING:
                                MessagesCore.CLAIM_CREATE_FAILED_OTHERCLAIM.send(p);
                                claimCreation.lock(); //Lock us from making another claim using this item
                                break;
                            default:
                                Message.sms(p, "An Error Happened!", null);
                                claimCreation.lock(); //Lock us from making another claim using this item
                        }
                    } else { //Overlapping
                        MessagesCore.CLAIM_CREATE_FAILED_OTHERCLAIM.send(p);
                        claimCreation.lock(); //Lock us from making another claim using this item
                    }
                } else {
                    Visualization.fromLocation(loc, p.getLocation().getBlockY(), p.getLocation()).apply(p);
                }
                if (claimCreation.locked)
                    cancelCreation(p, 2L);
            } else if (error == CLAIM_ERRORS.LOCATION_ALREADY_EXISTS) {
                Visualization.fromLocation(loc, p.getLocation().getBlockY(), p.getLocation()).apply(p);
            } else if (error == CLAIM_ERRORS.OVERLAPPING) {
                MessagesCore.CLAIM_CREATE_FAILED_OTHERCLAIM.send(p);
                claimCreation.lock();
                cancelCreation(p, 2L);
            }
        }
    }

    private void cancelCreation(Player p, Long time) {
        if (cancelTimers.containsKey(p))
            Bukkit.getScheduler().cancelTask(cancelTimers.get(p));
        cancelTimers.put(p, Bukkit.getScheduler().scheduleSyncDelayedTask(Pueblos.getInstance(), () -> {
            listener.claimCreation.remove(p);
        }, time * 20L));
    }
}
