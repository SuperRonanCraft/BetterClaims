package me.RonanCraft.BetterClaims.customevents;

import me.RonanCraft.BetterClaims.claims.ClaimData;
import org.bukkit.entity.Player;

//Called when a claim has successfully uploaded to the database and is now being protected
public class ClaimEvent_ClaimCreate extends ClaimEventType_Claim {

    private final Player creator;

    /**
     * Broadcasts this new claim that was created
     * @param claimData The claim that was just created
     * @param creator The player who created this claim
     */
    public ClaimEvent_ClaimCreate(ClaimData claimData, Player creator) {
        super(claimData, true);
        this.creator = creator;
    }

    public Player getCreator() {
        return creator;
    }
}
