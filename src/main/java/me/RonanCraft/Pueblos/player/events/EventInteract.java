package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.*;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
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
        if (claim != null) {
            CLAIM_FLAG flag = null;
            if (block.getType().name().contains("LEVER")) {
                flag = CLAIM_FLAG.ALLOW_LEVER;
            } else if (block.getType().name().contains("DOOR")) {
                flag = CLAIM_FLAG.ALLOW_DOOR;
            } else if (block.getType().name().contains("BUTTON")) {
                flag = CLAIM_FLAG.ALLOW_BUTTON;
            }
            if (flag != null) {
                //Check member flag value (if it exists)
                CLAIM_FLAG_MEMBER memberFlag = flag.getMemberEquivalent();
                ClaimMember member = claim.getMember(e.getPlayer());
                Object flagValue = claim.getFlags().getFlag(flag);
                if (memberFlag != null && member != null && member.getFlags().containsKey(memberFlag))
                    flagValue = member.getFlags().get(memberFlag);
                e.setCancelled(!(Boolean) flagValue);
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
                                Messages.core.sendClaimCreateSuccess(p);
                                Visualization.fromClaim(claim, p.getLocation().getBlockY(), VisualizationType.CLAIM, p.getLocation()).apply(p);
                                claimCreation.lock(); //Lock us from making another claim using this item
                                break;
                            case SIZE:
                                Messages.core.sendClaimCreateFailedSize(p);
                                break;
                            case OVERLAPPING:
                                Messages.core.sendClaimCreateFailedOtherClaim(p);
                                claimCreation.lock(); //Lock us from making another claim using this item
                                break;
                            default:
                                Messages.core.sms(p, "An Error Happened!");
                                claimCreation.lock(); //Lock us from making another claim using this item
                        }
                    } else { //Overlapping
                        Messages.core.sendClaimCreateFailedOtherClaim(p);
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
                Messages.core.sendClaimCreateFailedOtherClaim(p);
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
            p.sendMessage("Cancelled!");
        }, time * 20L));
    }
}
