package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.resources.claims.ClaimMain;
import org.bukkit.entity.Player;

//Called when a player walks IN to a claim
public class PueblosEvent_ClaimWalkedIn extends PueblosEventType_ClaimCancellable {

    private final Player player;

    public PueblosEvent_ClaimWalkedIn(ClaimMain claim, Player player) {
        super(claim);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

}
