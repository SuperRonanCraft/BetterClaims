package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;

//Called when a player walks IN to a claim
public class PueblosEvent_ClaimWalkedIn extends PueblosEventType_ClaimCancellable {

    private final Player player;

    public PueblosEvent_ClaimWalkedIn(Claim claim, Player player) {
        super(claim);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

}
