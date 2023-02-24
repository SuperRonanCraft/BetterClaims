package me.RonanCraft.BetterClaims.customevents;

import me.RonanCraft.BetterClaims.claims.Claim;
import org.bukkit.entity.Player;

//Called when a player walks IN to a claim
public class ClaimEvent_ClaimWalkedIn extends ClaimEventType_ClaimCancellable {

    private final Player player;

    public ClaimEvent_ClaimWalkedIn(Claim claim, Player player) {
        super(claim, false);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

}
