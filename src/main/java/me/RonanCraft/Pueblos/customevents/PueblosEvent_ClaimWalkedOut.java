package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.resources.claims.ClaimMain;
import org.bukkit.entity.Player;

//Called when a player walks OUT of a claim
public class PueblosEvent_ClaimWalkedOut extends PueblosEventType_ClaimCancellable {

    private final Player player;

    public PueblosEvent_ClaimWalkedOut(ClaimMain claim, Player player) {
        super(claim, false);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

}
