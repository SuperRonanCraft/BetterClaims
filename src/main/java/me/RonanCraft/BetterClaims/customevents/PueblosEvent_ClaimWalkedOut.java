package me.RonanCraft.BetterClaims.customevents;

import me.RonanCraft.BetterClaims.claims.Claim;
import org.bukkit.entity.Player;

//Called when a player walks OUT of a claim
public class PueblosEvent_ClaimWalkedOut extends PueblosEventType_ClaimCancellable {

    private final Player player;

    public PueblosEvent_ClaimWalkedOut(Claim claim, Player player) {
        super(claim, false);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

}
