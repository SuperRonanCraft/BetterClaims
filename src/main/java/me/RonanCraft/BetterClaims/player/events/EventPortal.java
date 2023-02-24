package me.RonanCraft.BetterClaims.player.events;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.world.PortalCreateEvent;

import java.util.ArrayList;
import java.util.List;

public class EventPortal implements PueblosEvents {
    //(Added v1.2.0)
    //Stop portal creations when target is inside a claim
    void onPortal(PortalCreateEvent e) {
        if (e.getReason() != PortalCreateEvent.CreateReason.OBC_DESTINATION)
            return;
        List<Location> top_locs = new ArrayList<>();
        for (Block block : e.getBlocks()) { //Add all blocks to check
            Location loc = block.getLocation();
            boolean add = true;
            for (Location top_loc : top_locs) { //Just check the tallest blocks of the portal
                if (top_loc.getBlockZ() == loc.getBlockZ() && top_loc.getBlockX() == loc.getBlockX()) { //X & Y equal to each other (also replace if Y is taller)
                    if (loc.getBlockY() > top_loc.getBlockY())
                        top_locs.set(top_locs.indexOf(top_loc), loc);
                    add = false;
                    break;
                }
            }
            if (add)
                top_locs.add(loc);
        }
        for (Location loc : top_locs)
            if (getClaimAt(loc, true) != null) {
                e.setCancelled(true);
                return;
            }
    }
}
