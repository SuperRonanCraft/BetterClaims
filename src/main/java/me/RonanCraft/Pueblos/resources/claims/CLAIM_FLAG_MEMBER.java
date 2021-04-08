package me.RonanCraft.Pueblos.resources.claims;

public enum CLAIM_FLAG_MEMBER {
    ALLOW_CHEST(Boolean.class, true),
    ALLOW_DOOR(Boolean.class, true),
    ALLOW_LEVER(Boolean.class, true),
    ALLOW_BUTTON(Boolean.class, true),
    ALLOW_BED(Boolean.class, true);

    private final Class<?> type;
    private final Object defaultValue;

    CLAIM_FLAG_MEMBER(Class<?> _type, Object defaultValue) {
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
