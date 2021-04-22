package me.RonanCraft.Pueblos.resources.files.msgs;

public enum MessagesHelp {

    HELP("Help"),
    RELOAD("Reload"),
    CREATE("Create"),
    INFO("Info"),
    LIST("List"),
    REQUEST("Request"),
    FLAGS("Flags"),
    PREFIX("Prefix"),
    ADMIN_CLAIM("AdminClaim");

    String section;

    MessagesHelp(String section) {
        this.section = section;
    }

    private static final String pre = "Help.";

    public String get() {
        return Message.getLang().getString(pre + section);
    }
}
