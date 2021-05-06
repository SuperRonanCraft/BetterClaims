package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.player.data.PlayerData;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_MODE;
import me.RonanCraft.Pueblos.resources.claims.ClaimMain;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
import me.RonanCraft.Pueblos.resources.tools.visual.Visualization;
import me.RonanCraft.Pueblos.resources.tools.visual.VisualizationType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class EventItemChange implements PueblosEvents {

    private final EventListener listener;
    private final HashMap<Player, Integer> claimShowing = new HashMap<>();

    EventItemChange(EventListener listener) {
        this.listener = listener;
    }

    //Show claiming message when claim item is equipped and start a `claimInteraction`
    void onItemChange(PlayerItemHeldEvent e) {
        PlayerData data = listener.getPlayerData(e.getPlayer());
        data.removeClaimInteraction();
        if (claimShowing.containsKey(e.getPlayer()))
            return;
        ItemStack item = e.getPlayer().getInventory().getItem(e.getNewSlot());
        if (item != null && item.getType().equals(getClaimItem()) && data.getClaimInteraction() == null) {
            data.setClaimInteraction(new PlayerClaimInteraction(e.getPlayer(), CLAIM_MODE.CREATE));
            Claim claim = getClaimAt(e.getPlayer().getLocation(), true);
            showClaimLater(claim, e.getPlayer());
        } else
            data.removeClaimInteraction();
    }

    //Delay showing the claim and message to not spam player scrolling through inventory
    private void showClaimLater(Claim claim, Player p) {
        claimShowing.put(p,
            Bukkit.getScheduler().scheduleSyncDelayedTask(Pueblos.getInstance(), () -> {
                ItemStack item = p.getInventory().getItemInMainHand();
                if (item.getType().equals(getClaimItem())) {
                    if (claim != null) {
                        VisualizationType visType = VisualizationType.ERROR;
                        Visualization vis = new Visualization();
                        if (claim.isMember(p) || (claim.isAdminClaim() && PermissionNodes.ADMIN_CLAIM.check(p))) {
                            MessagesCore.CLAIM_ITEM_INCLAIM.send(p);
                            visType = VisualizationType.CLAIM;
                        } else
                            MessagesCore.CLAIM_ITEM_NOTOWNER.send(p);
                        Visualization.fromClaim(claim, p.getLocation().getBlockY(), visType, p.getLocation()).apply(p);
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
