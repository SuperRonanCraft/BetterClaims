package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMain;
import org.bukkit.entity.Player;

//Called when a claim has successfully uploaded to the database and is now being protected
public class PueblosEvent_ClaimCreate extends PueblosEventType_Claim {

    private final Player creator;

    /**
     * Broadcasts this new claim that was created
     * @param claim The claim that was just created
     * @param creator The player who created this claim
     */
    public PueblosEvent_ClaimCreate(Claim claim, Player creator) {
        super(claim);
        this.creator = creator;
    }

    public Player getCreator() {
        return creator;
    }
}
