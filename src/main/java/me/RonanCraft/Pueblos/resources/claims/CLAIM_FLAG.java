package me.RonanCraft.Pueblos.resources.claims;

public enum CLAIM_FLAG {
    PVP(Boolean.class, false),
    TNT(Boolean.class, false),
    ALLOW_DOOR(Boolean.class, false),
    ALLOW_LEVER(Boolean.class, false),
    ALLOW_BUTTON(Boolean.class, false),
    ALLOW_BED(Boolean.class, false);

    private final Class<?> type;
    private final Object defaultValue;

    CLAIM_FLAG(Class<?> _type, Object defaultValue) {
        this.type = _type;
        this.defaultValue = defaultValue;
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
}
