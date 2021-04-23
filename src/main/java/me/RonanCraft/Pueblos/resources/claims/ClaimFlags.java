package me.RonanCraft.Pueblos.resources.claims;

import java.util.HashMap;

public class ClaimFlags {
    HashMap<CLAIM_FLAG, Object> flags = new HashMap<>();

    private final ClaimInfo claim;

    ClaimFlags(ClaimInfo claim) {
        this.claim = claim;
    }

    public void setFlag(CLAIM_FLAG flag, Object value, boolean update) {
        flags.put(flag, value);
        if (update)
            claim.updated();
    }

    public Object getFlag(CLAIM_FLAG flag) {
        return flags.getOrDefault(flag, flag.getDefault());
    }

    public HashMap<CLAIM_FLAG, Object> getFlags() {
        return flags;
    }
}
