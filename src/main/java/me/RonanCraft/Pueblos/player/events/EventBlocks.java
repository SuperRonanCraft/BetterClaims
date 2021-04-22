package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;

public class EventBlocks {

    private final EventListener listener;

    EventBlocks(EventListener listener) {
        this.listener = listener;
    }

    //Player Block Break
    void onBreak(BlockBreakEvent e) {
        if (!e.isCancelled())
            e.setCancelled(cantBuilt(e, e.getPlayer()));
    }

    //Player Block Place
    void onPlace(BlockPlaceEvent e) {
        if (!e.isCancelled())
            e.setCancelled(cantBuilt(e, e.getPlayer()));
    }

    boolean cantBuilt(BlockEvent e, Player p) {
        Claim claim = listener.getClaim(e.getBlock().getLocation());
        if (claim == null)
            return false;
        else if (claim.isAdminClaim() && PermissionNodes.ADMIN_CLAIM.check(p))
            return false;
        else
            return !claim.isMember(p);
    }

}
