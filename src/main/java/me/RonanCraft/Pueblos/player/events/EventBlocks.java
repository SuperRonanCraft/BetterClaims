package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;

public class EventBlocks implements PueblosEvents {

    //Player Block Break
    void onBreak(BlockBreakEvent e) {
        if (!e.isCancelled())
            e.setCancelled(!allowBreak(e.getPlayer(), e.getBlock().getLocation()));
    }

    //Player Block Place
    void onPlace(BlockPlaceEvent e) {
        if (!e.isCancelled())
            e.setCancelled(!allowBreak(e.getPlayer(), e.getBlock().getLocation()));
    }

}
