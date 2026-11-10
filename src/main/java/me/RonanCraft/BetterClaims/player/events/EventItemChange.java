package me.RonanCraft.BetterClaims.player.events;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.player.data.PlayerData;
import me.RonanCraft.BetterClaims.resources.helper.HelperPlayer;
import me.RonanCraft.BetterClaims.resources.PermissionNodes;
import me.RonanCraft.BetterClaims.claims.ClaimData;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_MODE;
import me.RonanCraft.BetterClaims.resources.messages.MessagesCore;
import me.RonanCraft.BetterClaims.resources.helper.HelperClaim;
import me.RonanCraft.BetterClaims.resources.visualization.Visualization;
import me.RonanCraft.BetterClaims.resources.visualization.VisualizationType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import com.tcoded.folialib.wrapper.task.WrappedTask;

public class EventItemChange implements ClaimEvents {

    private final EventListener listener;

    EventItemChange(EventListener listener) {
        this.listener = listener;
    }

    private final java.util.concurrent.ConcurrentMap<Player, WrappedTask> claimShowing = new java.util.concurrent.ConcurrentHashMap<>();

    //Show claiming message when claim item is equipped and start a `claimInteraction`
    void onItemChange(PlayerItemHeldEvent e) {
        if (!HelperClaim.worldEnabled(e.getPlayer().getWorld()))
            return;
        PlayerData data = HelperPlayer.getData(e.getPlayer());
        data.removeClaimInteraction();
        if (claimShowing.containsKey(e.getPlayer()))
            return;
        ItemStack item = e.getPlayer().getInventory().getItem(e.getNewSlot());
        if (item != null && item.getType().equals(getClaimItem()) && data.getClaimInteraction() == null) {
            data.setClaimInteraction(new PlayerClaimInteraction(e.getPlayer(), CLAIM_MODE.CREATE));
            ClaimData claimData = getClaimAt(e.getPlayer().getLocation(), true);
            showClaimLater(claimData, e.getPlayer());
        } else
            data.removeClaimInteraction();
    }

    //Delay showing the claim and message to not spam player scrolling through inventory
    private void showClaimLater(ClaimData claimData, Player p) {
        claimShowing.put(p,
            BetterClaims.getInstance().getTasks().later(p, () -> {
                ItemStack item = p.getInventory().getItemInHand();//.getItemInMainHand();
                if (item.getType().equals(getClaimItem())) {
                    if (claimData != null) {
                        VisualizationType visType = VisualizationType.ERROR;
                        Visualization vis = new Visualization();
                        if (claimData.isMember(p) || (claimData.isAdminClaim() && PermissionNodes.ADMIN_CLAIM.check(p))) {
                            MessagesCore.CLAIM_ITEM_INCLAIM.send(p);
                            visType = VisualizationType.CLAIM;
                        } else
                            MessagesCore.CLAIM_ITEM_NOTOWNER.send(p);
                        Visualization.fromClaim(claimData, p.getLocation().getBlockY(), visType, p.getLocation()).apply(p);
                    } else {
                        MessagesCore.CLAIM_ITEM_NOCLAIM.send(p);
                    }
                    claimShowing.put(p,
                        BetterClaims.getInstance().getTasks().later(p, () -> {
                            claimShowing.remove(p);
                        }, 20L * 10));
                } else
                    claimShowing.remove(p);
            }, 5L));
    }

    private Material getClaimItem() {
        return listener.interact.claim_item;
    }

    void forget(Player player) {
        WrappedTask task = claimShowing.remove(player);
        if (task != null) task.cancel();
    }
}
