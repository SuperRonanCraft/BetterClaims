package me.RonanCraft.Pueblos.player.events;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
    @EventHandler(priority = EventPriority.HIGHEST)
    void onBreak(BlockBreakEvent e) {
        if (listener.isProtected(e.getPlayer(), e.getBlock().getLocation()))
            e.setCancelled(true);
    }

    //Player Block Place
    @EventHandler (priority = EventPriority.HIGHEST)
    void onPlace(BlockPlaceEvent e) {
        if (listener.isProtected(e.getPlayer(), e.getBlock().getLocation()))
            e.setCancelled(true);
    }

}
