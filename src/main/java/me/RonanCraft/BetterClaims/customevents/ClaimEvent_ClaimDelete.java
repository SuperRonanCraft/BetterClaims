package me.RonanCraft.BetterClaims.customevents;

import me.RonanCraft.BetterClaims.claims.Claim_Child;
import me.RonanCraft.BetterClaims.claims.Claim;

import java.util.List;

//Called when a claim is basically disabled by being removed from the claim list and successfully deleted from the database
public class ClaimEvent_ClaimDelete extends ClaimEventType_Claim {

    public ClaimEvent_ClaimDelete(Claim claim, List<Claim_Child> children) {
        super(claim, true);
    }
}
