package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.resources.claims.ClaimChild;
import me.RonanCraft.Pueblos.resources.claims.ClaimMain;

import java.util.List;

//Called when a claim is basically disabled by being removed from the claim list and successfully deleted from the database
public class PueblosEvent_ClaimDelete extends PueblosEventType_Claim {

    public PueblosEvent_ClaimDelete(ClaimMain claim, List<ClaimChild> children) {
        super(claim, true);
    }
}
