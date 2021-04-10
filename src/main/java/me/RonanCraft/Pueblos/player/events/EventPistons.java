package me.RonanCraft.Pueblos.player.events;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventPistons {

    private final EventListener listener;

    EventPistons(EventListener listener) {
        this.listener = listener;
    }

    void onPiston(BlockPistonExtendEvent e) {
        pistonEvent(e, e.getBlocks(), e.getDirection());
    }

    void onPiston(BlockPistonRetractEvent e) {
        pistonEvent(e, e.getBlocks(), e.getDirection());
    }

    private void pistonEvent(BlockPistonEvent e, List<Block> blocks, BlockFace dir) {
        List<Location> locations = new ArrayList<>();
        locations.add(e.getBlock().getLocation());
        blocks.forEach(block -> locations.add(block.getLocation()));
        blocks: for (Block block : blocks) { //Add all modified locations as well (fixes pulling blocks from outside claim)
            Location loc = block.getLocation().clone();
            loc.add(dir.getModX(), dir.getModY(), dir.getModZ());
            //Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.BARRIER, loc.clone().add(0.5, 1.5, 0.5), 1);
            for (Location _loc : locations)
                if (_loc.getBlockX() == loc.getBlockX() && _loc.getBlockZ() == loc.getBlockZ()) {
                    break blocks;
                }
            locations.add(loc);
        }
        if (locations.size() <= 1) //Ignore pistons not moving anything
            return;

        //Blocks going between claimed and unclaimed land
        HashMap<Location, Boolean> locations_protected = new HashMap<>();
        for (Location loc : locations)
            locations_protected.put(loc, listener.isProtected(loc));

        //Blocks going between unprotected and protected land
        boolean all_match = (locations_protected.containsValue(true) && !locations_protected.containsValue(false))
                || (locations_protected.containsValue(false) && !locations_protected.containsValue(true));
        if (!all_match) { //Not all match true or false
            e.setCancelled(true);
        }
    }

}
