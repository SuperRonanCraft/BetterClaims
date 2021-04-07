package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.player.command.types.CmdCreate;
import me.RonanCraft.Pueblos.resources.claims.*;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import me.RonanCraft.Pueblos.resources.tools.visual.Visualization;
import me.RonanCraft.Pueblos.resources.tools.visual.VisualizationType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class EventInteract {

    private final EventListener listener;

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
            if (block.getType().name().contains("LEVER")) {
                e.setCancelled(!(Boolean) claim.getFlags().getFlag(CLAIM_FLAG.ALLOW_LEVER));
            } else if (block.getType().name().contains("DOOR")) {
                e.setCancelled(!(Boolean) claim.getFlags().getFlag(CLAIM_FLAG.ALLOW_DOOR));
            } else if (block.getType().name().contains("BUTTON")) {
                e.setCancelled(!(Boolean) claim.getFlags().getFlag(CLAIM_FLAG.ALLOW_BUTTON));
            }
        }
    }

    void onInteractCreateClaim(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null
                || e.getItem() == null
                || e.isCancelled()
                || e.getAction() != Action.RIGHT_CLICK_BLOCK
                || !e.getItem().getType().equals(Material.GOLDEN_SHOVEL))
            return;

        e.setCancelled(true);
        Player p = e.getPlayer();
        if (!listener.claimCreation.containsKey(p))
            listener.claimCreation.put(p, new PlayerClaimCreation(p));
        PlayerClaimCreation claimCreation = listener.claimCreation.get(p);
        Location loc = e.getClickedBlock().getLocation();
        if (!claimCreation.locked && claimCreation.addLocation(loc)) {
            List<Location> corners = claimCreation.locations;
            if (corners.size() >= 2) {
                ClaimHandler handler = Pueblos.getInstance().getSystems().getClaimHandler();
                Claim claim = handler.claimCreate(p.getUniqueId(), p.getName(), new ClaimPosition(p.getWorld(), corners.get(0), corners.get(1)));
                if (claim != null) {
                    CLAIM_ERRORS error = handler.addClaim(claim, p, true);
                    switch (error) {
                        case NONE: Messages.core.sendClaimCreateSuccess(p); break;
                        case SIZE: Messages.core.sendClaimCreateFailedSize(p); break;
                        case OVERLAPPING: Messages.core.sendClaimCreateFailedOtherClaim(p); break;
                        default: Messages.core.sms(p, "An Error Happened!");
                    }
                    Visualization.fromClaim(claim, p.getLocation().getBlockY(), VisualizationType.Claim, p.getLocation()).apply(p);
                } else //Overlapping
                    Messages.core.sendClaimCreateFailedOtherClaim(p);
                claimCreation.lock(); //Lock us from making another claim using this item
            } else {
                //CmdCreate.showCorners(p, e.getClickedBlock());
            }
        }
    }

}
