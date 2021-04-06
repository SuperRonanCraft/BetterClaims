package me.RonanCraft.Pueblos.resources.files.msgs;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.files.FileLanguage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface Message {
    default FileLanguage getLang() {
        return Pueblos.getInstance().getFiles().getLang();
    }

    default void sms(CommandSender sendi, String msg) {
        if (!msg.equals(""))
            sendi.sendMessage(colorPre(msg));
    }

    default void sms(CommandSender sendi, List<String> msg) {
        if (msg != null && !msg.isEmpty()) {
            msg.forEach(str -> msg.set(msg.indexOf(str), color(str)));
            sendi.sendMessage(msg.toArray(new String[0]));
        }
    }

    private String getPrefix() {
        return getLang().getString("Messages.Prefix");
    }

    default String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    default String colorPre(String str) {
        return color(getPrefix() + str);
    }
}
