package me.RonanCraft.Pueblos.resources.claims;

import java.util.HashMap;

public class ClaimFlags {
    HashMap<CLAIM_FLAG, Object> flags = new HashMap<>();

    public void setFlag(CLAIM_FLAG flag, Object value) {
        flags.put(flag, value);
    }

    public Object getFlag(CLAIM_FLAG flag) {
        return flags.getOrDefault(flag, getDefault(flag));
    }

    private Object getDefault(CLAIM_FLAG flag) {
        switch (flag) {
            case ALLOW_DOOR:
            case ALLOW_BUTTON: return true;
            default: return false;
        }
    }

}
