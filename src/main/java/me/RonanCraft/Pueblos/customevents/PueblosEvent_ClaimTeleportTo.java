package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMain;
import org.bukkit.Location;
import org.bukkit.entity.Player;

//Called when a owner/member teleports to a claim
public class PueblosEvent_ClaimTeleportTo extends PueblosEventType_ClaimCancellable {

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
