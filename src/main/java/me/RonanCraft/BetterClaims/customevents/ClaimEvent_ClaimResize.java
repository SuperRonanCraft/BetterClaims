package me.RonanCraft.BetterClaims.customevents;

import me.RonanCraft.BetterClaims.claims.ClaimData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

//Called when a claim has successfully resized
public class ClaimEvent_ClaimResize extends ClaimEventType_ClaimCancellable {

    private final Player editor;
    private final Vector newLoc_1;
    private final Vector newLoc_2;

    /**
     * Broadcasts this new claim that was created
     * @param claimData The claim that was just created
     * @param editor The player who is changing the size of this claim
     */
    public ClaimEvent_ClaimResize(ClaimData claimData, Player editor, Vector loc_1, Vector loc_2) {
        super(claimData, true);
        this.editor = editor;
        this.newLoc_1 = loc_1;
        this.newLoc_2 = loc_2;
    }

    //Required

    public Vector getNewLoc_1() {
        return newLoc_1;
    }

    public Vector getNewLoc_2() {
        return newLoc_2;
    }

    public Player getEditor() {
        return editor;
    }

}
