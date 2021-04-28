package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.entity.Player;

//Called when a player walks OUT of a claim
public class PueblosEvent_ClaimWalkedOut extends PueblosEventType_ClaimCancellable {

    private final Player player;

    public PueblosEvent_ClaimWalkedOut(Claim claim, Player player) {
        super(claim);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

}
