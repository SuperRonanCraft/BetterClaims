package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.claims.CLAIM_ERRORS;
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
            Player p = e.getPlayer();
            p.sendMessage("Auction Sign!");
            int _claim_id = getInt(e.getLine(1));
            if (_claim_id >= 0) {
                Claim claim = getPl().getClaimHandler().getClaim(_claim_id);
                if (claim != null) {
                    if (!claim.contains(e.getBlock().getLocation()))
                        p.sendMessage("Auction sign is too far from this claim!");
                } else {
                    p.sendMessage("Claim id " + e.getLine(1) + " is not a valid Claim id!");
                }
            } else {
                p.sendMessage("Claim id " + e.getLine(1) + " is not a valid Claim id!");
            }
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

    private int getInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (IllegalArgumentException e) {
            return -1;
        }
    }

}
