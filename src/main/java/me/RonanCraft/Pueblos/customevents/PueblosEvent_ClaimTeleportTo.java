package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PueblosEvent_ClaimTeleportTo extends ClaimEvent implements Cancellable {

    private final Player player;
    private final Location from_loc;

    public PueblosEvent_ClaimTeleportTo(Claim claim, Player player, Location from_loc) {
        super(claim);
        this.player = player;
        this.from_loc = from_loc;
    }

    //Required

    public Location getFrom_loc() {
        return from_loc;
    }

    public Player getPlayer() {
        return player;
    }
}
