package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.claims.Claim_Child;
import me.RonanCraft.Pueblos.claims.Claim;

import java.util.List;

//Called when a claim is basically disabled by being removed from the claim list and successfully deleted from the database
public class PueblosEvent_ClaimDelete extends PueblosEventType_Claim {

    public PueblosEvent_ClaimDelete(Claim claim, List<Claim_Child> children) {
        super(claim, true);
    }
}
