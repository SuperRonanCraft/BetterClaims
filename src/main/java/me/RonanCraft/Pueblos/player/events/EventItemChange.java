package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
import me.RonanCraft.Pueblos.resources.tools.visual.Visualization;
import me.RonanCraft.Pueblos.resources.tools.visual.VisualizationType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class EventItemChange {

    private final EventListener listener;
    private final HashMap<Player, Integer> claimShowing = new HashMap<>();

    EventItemChange(EventListener listener) {
        this.listener = listener;
    }

    void onItemChange(PlayerItemHeldEvent e) {
        listener.claimInteraction.remove(e.getPlayer());
        if (claimShowing.containsKey(e.getPlayer()))
            return;
        ItemStack item = e.getPlayer().getInventory().getItem(e.getNewSlot());
        if (item != null && item.getType().equals(getClaimItem()) && !listener.claimInteraction.containsKey(e.getPlayer())) {
            //Messages.core.sms(e.getPlayer(), "Shovel!");
            Claim claim = listener.getClaim(e.getPlayer().getLocation());
            showClaimLater(claim, e.getPlayer());
        } else
            listener.claimInteraction.remove(e.getPlayer());
    }

    void showClaimLater(Claim claim, Player p) {
        claimShowing.put(p,
            Bukkit.getScheduler().scheduleSyncDelayedTask(Pueblos.getInstance(), () -> {
                ItemStack item = p.getInventory().getItemInMainHand();
                if (item.getType().equals(getClaimItem())) {
                    if (claim != null) {
                        if (claim.isMember(p))
                            MessagesCore.CLAIM_ITEM_INCLAIM.send(p);
                        else
                            MessagesCore.CLAIM_ITEM_NOTOWNER.send(p);
                        Visualization.fromClaim(claim, p.getLocation().getBlockY(), claim.isMember(p) ? VisualizationType.CLAIM : VisualizationType.ERROR, p.getLocation()).apply(p);
                    } else {
                        MessagesCore.CLAIM_ITEM_NOCLAIM.send(p);
                    }
                    claimShowing.put(p,
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Pueblos.getInstance(), () -> {
                            claimShowing.remove(p);
                        }, 20L * 10));
                } else
                    claimShowing.remove(p);
            }, 5L));
    }

    private Material getClaimItem() {
        return listener.interact.claim_item;
    }

}
