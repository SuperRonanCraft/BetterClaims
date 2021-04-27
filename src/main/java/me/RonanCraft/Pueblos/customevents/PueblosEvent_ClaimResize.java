package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.resources.claims.ClaimInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;

//Called when a claim has successfully resized
public class PueblosEvent_ClaimResize extends PueblosEventType_ClaimCancellable {

    private final Player editor;
    private final Location newLoc_1;
    private final Location newLoc_2;

    /**
     * Broadcasts this new claim that was created
     * @param claim The claim that was just created
     * @param editor The player who is changing the size of this claim
     */
    public PueblosEvent_ClaimResize(ClaimInfo claim, Player editor, Location loc_1, Location loc_2) {
        super(claim);
        this.editor = editor;
        this.newLoc_1 = loc_1;
        this.newLoc_2 = loc_2;
    }

    //Required

    public Location getNewLoc_1() {
        return newLoc_1;
    }

    public Location getNewLoc_2() {
        return newLoc_2;
    }

    public Player getEditor() {
        return editor;
    }

}
