package me.RonanCraft.Pueblos.resources.claims;

public enum CLAIM_FLAG {
    PVP(Boolean.class),
    TNT(Boolean.class),
    ALLOW_DOOR(Boolean.class),
    ALLOW_BUTTON(Boolean.class),
    ALLOW_BED(Boolean.class);

    public Class<?> type;

    CLAIM_FLAG(Class<?> type) {
        this.type = type;
    }
}
