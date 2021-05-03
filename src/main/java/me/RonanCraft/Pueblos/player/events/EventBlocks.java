package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;

public class EventBlocks implements PueblosEvents {

    //Player Block Break
    void onBreak(BlockBreakEvent e) {
        if (!e.isCancelled() && !allowBreak(e.getPlayer(), e.getBlock().getLocation()))
            e.setCancelled(true);
    }

    //Player Block Place
    void onPlace(BlockPlaceEvent e) {
        if (!e.isCancelled() && !allowBreak(e.getPlayer(), e.getBlock().getLocation()))
            e.setCancelled(true);
    }

    //When a player changes a sign
    void onSignChange(SignChangeEvent e) {
        if (e.isCancelled())
            return;
        if (isATitle(e.getLine(0))) {

        }
    }

    private final String[] titles = {"[Auction]", "[Auctions]", "[Sell]", "[Rent]"};

    private boolean isATitle(String str) {
        for (String title : titles) {
            if (str.equalsIgnoreCase(title))
                return true;
        }
        return false;
    }

}
