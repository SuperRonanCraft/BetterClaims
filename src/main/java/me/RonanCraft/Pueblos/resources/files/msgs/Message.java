package me.RonanCraft.Pueblos.resources.files.msgs;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.files.FileLanguage;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public interface Message {
    default FileLanguage getLang() {
        return Pueblos.getInstance().getFiles().getLang();
    }

    default void sms(CommandSender sendi, String msg) {
        if (!msg.equals(""))
            sendi.sendMessage(colorPre(sendi, msg));
    }

    default void sms(CommandSender sendi, List<String> msg) {
        if (msg != null && !msg.isEmpty()) {
            msg.forEach(str -> msg.set(msg.indexOf(str), color(sendi, str)));
            sendi.sendMessage(msg.toArray(new String[0]));
        }
    }

    private String getPrefix() {
        return getLang().getString("Messages.Prefix");
    }

    default String color(CommandSender p, String str) {
        if (str != null && p instanceof Player)
            str = getPh(p, str);
        if (str != null)
            return ChatColor.translateAlternateColorCodes('&', str);
        return null;
    }

    private String getPh(CommandSender p, String str) {
        if (Pueblos.getInstance().PlaceholderAPI)
            try {
                str = PlaceholderAPI.setPlaceholders((Player) p, str);
            } catch (Exception e) {
                //Something went wrong with PAPI
            }
        if (str.contains("%player_name%"))
            str = str.replaceAll("%player_name%", p.getName());
        if (str.contains("%player_uuid%"))
            if (p instanceof Player)
                str = str.replaceAll("%player_uuid%", ((Player) p).getUniqueId().toString());
        return str;
    }

    default String colorPre(CommandSender p, String str) {
        return color(p, getPrefix() + str);
    }
}
