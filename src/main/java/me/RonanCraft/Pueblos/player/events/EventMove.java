package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.tools.HelperEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;

public class EventMove implements PueblosEvents {

    private final EventListener listener;

    EventMove(EventListener listener) {
        this.listener = listener;
    }

    //Trigger event when walking in/out of a claim
    void onMove(PlayerMoveEvent e) {
        if (e.getFrom().equals(e.getTo())) //Didn't really move much
            return;
        Player p = e.getPlayer();
        Location loc = e.getTo();
        if (listener.insideClaim.containsKey(p)) {
            Claim claim = listener.insideClaim.get(p);
            if (claim.deleted)
                listener.insideClaim.remove(p);
            else if (!claim.contains(loc)) {
                listener.insideClaim.remove(p);
                HelperEvent.claimWalked(p, claim, false);
            }
        } else {
            Claim claim = getClaim(loc);
            if (claim != null) {
                listener.insideClaim.put(p, claim);
                HelperEvent.claimWalked(p, claim, true);
            }
        }
    }
}
