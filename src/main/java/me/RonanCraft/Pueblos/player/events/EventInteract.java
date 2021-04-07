package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.player.command.types.CmdCreate;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimHandler;
import me.RonanCraft.Pueblos.resources.claims.ClaimPosition;
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
        if (listener.isProtected(block.getLocation())) {
            if (block.getType().name().contains("SIGN")) {
                e.getPlayer().sendMessage("Sign!");
            } else if (block.getType().name().contains("DOOR")) {
                e.getPlayer().sendMessage("Door!");
            } else if (block.getType().name().contains("BUTTON")) {
                e.getPlayer().sendMessage("Button!");
            }
        }
    }

    void onInteractCreateClaim(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null || e.getItem() == null || e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        else if (!e.getItem().getType().equals(Material.GOLDEN_SHOVEL))
            return;
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
                    Messages.core.sms(p, "Claim Created!");
                    CmdCreate.showCorners(p, claim);
                    Visualization.fromClaim(claim, p.getLocation().getBlockY(), VisualizationType.Claim, p.getLocation()).apply(p);
                    handler.addClaim(claim, true);
                } else
                    Messages.core.sms(p, "Claim Error!");
                claimCreation.lock(); //Lock us from making another claim using this item
            } else {
                CmdCreate.showCorners(p, e.getClickedBlock());
            }
        }
        e.setCancelled(true);
    }

}
