package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.player.data.PlayerData;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.tools.HelperEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Objects;

public class EventMove implements PueblosEvents {

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
            Claim claim = getClaim(loc);
            if (claim != null) {
                data.setInsideClaim(claim);
                HelperEvent.claimWalked(p, claim, true);
            }
        }
    }
}
