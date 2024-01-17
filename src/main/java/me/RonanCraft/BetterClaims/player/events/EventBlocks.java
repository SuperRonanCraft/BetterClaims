package me.RonanCraft.BetterClaims.player.events;

import me.RonanCraft.BetterClaims.claims.ClaimData;
import me.RonanCraft.BetterClaims.resources.helper.HelperClaim;
import me.RonanCraft.BetterClaims.resources.messages.MessagesCore;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;

public class EventBlocks implements ClaimEvents {

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
                ClaimData claimData = HelperClaim.getHandler().getClaim(_claim_id);
                if (claimData != null) {
                    if (!claimData.contains(e.getBlock().getLocation()))
                        p.sendMessage("Auction sign is too far from this claim!");
                } else {
                    p.sendMessage("Claim id " + e.getLine(1) + " is not a valid Claim id!");
                }
            } else {
                MessagesCore.CLAIM_UNKNOWNID.send(p);
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
