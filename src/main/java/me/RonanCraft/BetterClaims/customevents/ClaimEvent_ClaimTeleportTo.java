package me.RonanCraft.BetterClaims.customevents;

import me.RonanCraft.BetterClaims.claims.ClaimData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

//Called when a owner/member teleports to a claim
public class ClaimEvent_ClaimTeleportTo extends ClaimEventType_ClaimCancellable {

    private final Player player;
    private final Location from_loc;

    public ClaimEvent_ClaimTeleportTo(ClaimData claimData, Player player, Location from_loc) {
        super(claimData, false);
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
