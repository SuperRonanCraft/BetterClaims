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
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EventItemChange {

    private final EventListener listener;

    EventItemChange(EventListener listener) {
        this.listener = listener;
    }

    void onItemChange(PlayerItemHeldEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItem(e.getNewSlot());
        if (item != null && item.getType().equals(getClaimItem()) && !listener.claimCreation.containsKey(e.getPlayer())) {
            Messages.core.sms(e.getPlayer(), "Shovel!");
            Claim claim = listener.getClaim(e.getPlayer().getLocation());
            if (claim != null && listener.isOwner(e.getPlayer(), claim))
                Visualization.fromClaim(claim, e.getPlayer().getLocation().getBlockY(), VisualizationType.Claim, e.getPlayer().getLocation()).apply(e.getPlayer());
        } else
            listener.claimCreation.remove(e.getPlayer());
    }

    private Material getClaimItem() {
        return Material.GOLDEN_SHOVEL;
    }

}
