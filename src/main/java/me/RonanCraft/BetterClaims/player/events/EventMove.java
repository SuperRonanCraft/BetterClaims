package me.RonanCraft.BetterClaims.player.events;

import me.RonanCraft.BetterClaims.player.data.PlayerData;
import me.RonanCraft.BetterClaims.claims.Claim;
import me.RonanCraft.BetterClaims.resources.helper.HelperEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class EventMove implements ClaimEvents {

    private final EventListener listener;

    EventMove(EventListener listener) {
        this.listener = listener;
    }

    //Trigger event when walking in/out of a claim
    void onMove(PlayerMoveEvent e) {
        if (e.getTo() == null || (e.getFrom().getBlockX() == e.getTo().getBlockX() &&
            e.getFrom().getBlockZ() == e.getTo().getBlockZ())) //Didn't really move
            return;
        Player p = e.getPlayer();
        Location loc = e.getTo();
        PlayerData data = listener.getPlayerData(p);
        if (data.getInsideClaim() != null) {
            Claim claim = data.getInsideClaim();
            if (claim.deleted)
                data.removeInsideClaim();
            else if (!claim.contains(loc)) {
                data.removeInsideClaim();
                HelperEvent.claimWalked(p, claim, false);
            }
        } else {
            Claim claim = getClaimMain(loc);
            if (claim != null) {
                data.setInsideClaim(claim);
                HelperEvent.claimWalked(p, claim, true);
            }
        }
    }
}
