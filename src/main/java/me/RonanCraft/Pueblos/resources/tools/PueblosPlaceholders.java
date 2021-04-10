package me.RonanCraft.Pueblos.resources.tools;

import me.RonanCraft.Pueblos.resources.claims.*;
import me.RonanCraft.Pueblos.resources.files.msgs.Message;
import org.bukkit.entity.Player;

public class PueblosPlaceholders {

    public static String getPlaceholder(String str, Player p, Object info) {
        if (info == null)
            return str;
        return Message.placeholder(p, str, info);
    }

}
