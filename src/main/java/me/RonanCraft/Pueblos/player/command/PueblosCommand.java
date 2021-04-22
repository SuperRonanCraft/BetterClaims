package me.RonanCraft.Pueblos.player.command;

import me.RonanCraft.Pueblos.Pueblos;
import org.bukkit.command.CommandSender;

public interface PueblosCommand {

    void execute(CommandSender sendi, String label, String[] args);

    boolean permission(CommandSender sendi);

    String getName();

    default boolean isPlayerOnly() {
        return false;
    }

    default Pueblos getPl() {
        return Pueblos.getInstance();
    }
}
