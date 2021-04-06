package me.RonanCraft.Pueblos.resources.files.msgs;

import org.bukkit.command.CommandSender;

public class MessagesCore implements Message {
    private static final String pre = "Messages.";

    public void sendReload(CommandSender sendi) {
        sms(sendi, getLang().getString(pre + "Reload"));
    }

    public void sendNoPermission(CommandSender sendi) {
        sms(sendi, getLang().getString(pre + "NoPermission"));
    }

    public void sendInvalidCommand(CommandSender sendi) {
        sms(sendi, getLang().getString(pre + "InvalidCommand"));
    }
}
