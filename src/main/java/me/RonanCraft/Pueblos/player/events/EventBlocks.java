package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventBlocks {

    private final EventListener listener;

    EventBlocks(EventListener listener) {
        this.listener = listener;
    }

    //Player Block Break
    void onBreak(BlockBreakEvent e) {
        if (!e.isCancelled())
            e.setCancelled(event(e, e.getPlayer()));
    }

    //Player Block Place
    void onPlace(BlockPlaceEvent e) {
        if (!e.isCancelled())
            e.setCancelled(event(e, e.getPlayer()));
    }

    boolean event(BlockEvent e, Player p) {
        Claim claim = listener.getClaim(e.getBlock().getLocation());
        return claim != null && !claim.isOwner(p);
    }

}
