package me.RonanCraft.BetterClaims.customevents;

import me.RonanCraft.BetterClaims.claims.ClaimData;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

//Called when a claim is about to be created (ie: uploaded and saved to the database)
public class PueblosEvent_ClaimAttemptCreate extends PueblosEventType_ClaimCancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player creator;

    /**
     * Broadcasts this new claim that was created
     * @param claimData The claim that was just created
     * @param creator The player who created this claim
     */
    public PueblosEvent_ClaimAttemptCreate(ClaimData claimData, Player creator) {
        super(claimData, true);
        this.creator = creator;
    }

    //Required
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getCreator() {
        return creator;
    }
}
