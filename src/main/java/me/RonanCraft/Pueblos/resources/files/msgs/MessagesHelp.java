package me.RonanCraft.Pueblos.resources.files.msgs;

public class MessagesHelp implements Message {
    private static final String pre = "Help.";

    public String getHelpPrefix() {
        return getLang().getString(pre + "Prefix");
    }

    public String getHelpHelp() {
        return getLang().getString(pre + "Help");
    }

    public String getHelpReload() {
        return getLang().getString(pre + "Reload");
    }

    public String getHelpCreate() {
        return getLang().getString(pre + "Create");
    }
}
