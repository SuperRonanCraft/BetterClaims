package me.RonanCraft.BetterClaims.resources.messages;

public enum MessagesHelp {

    HELP("Help"),
    RELOAD("Reload"),
    CREATE("Create"),
    INFO("Info"),
    LIST("List"),
    REQUEST("Request"),
    FLAGS("Flags"),
    PREFIX("Prefix"),
    CONVERT("Convert"),
    CLAIM_ITEM("ClaimItem"),
    ADMIN_CLAIM("AdminClaim"),
    ADMIN_OVERRIDE("AdminOverride"),
    AUCTION("Auction"),
    ;

    String section;

    MessagesHelp(String section) {
        this.section = section;
    }

    private static final String pre = "Help.";

    public String get() {
        return Message.getLang().getString(pre + section);
    }
}
