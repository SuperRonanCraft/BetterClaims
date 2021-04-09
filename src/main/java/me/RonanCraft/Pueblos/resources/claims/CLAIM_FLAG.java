package me.RonanCraft.Pueblos.resources.claims;

public enum CLAIM_FLAG {
    PVP(Boolean.class, false),
    TNT(Boolean.class, false),
    ALLOW_DOOR(Boolean.class, false, CLAIM_FLAG_MEMBER.ALLOW_DOOR),
    ALLOW_LEVER(Boolean.class, false, CLAIM_FLAG_MEMBER.ALLOW_LEVER),
    ALLOW_BUTTON(Boolean.class, false, CLAIM_FLAG_MEMBER.ALLOW_BUTTON),
    ALLOW_BED(Boolean.class, false, CLAIM_FLAG_MEMBER.ALLOW_BED),
    ALLOW_REQUESTS(Boolean.class, true);

    private final Class<?> type;
    private final Object defaultValue;
    private final CLAIM_FLAG_MEMBER memberEquivalent;

    CLAIM_FLAG(Class<?> type, Object defaultValue) {
        this(type, defaultValue, null);
    }

    CLAIM_FLAG(Class<?> type, Object defaultValue, CLAIM_FLAG_MEMBER memberEquivalent) {
        this.type = type;
        this.defaultValue = defaultValue;
        this.memberEquivalent = memberEquivalent;
    }

    public Object cast(String arg) {
        if (type == Boolean.class && (arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("false")))
            return Boolean.valueOf(arg);
        else if (type == Integer.class)
            return Integer.valueOf(arg);
        return null;
    }

    public Object getDefault() {
        return defaultValue;
    }

    public CLAIM_FLAG_MEMBER getMemberEquivalent() {
        return memberEquivalent;
    }
}
