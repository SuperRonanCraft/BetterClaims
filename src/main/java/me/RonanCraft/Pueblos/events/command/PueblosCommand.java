package me.RonanCraft.Pueblos.events.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface PueblosCommand {

    void execute(CommandSender sendi, String label, String[] args);

    List<String> tabComplete(CommandSender sendi, String[] args);

    boolean permission(CommandSender sendi);

    String getName();

    default boolean isPlayerOnly() {
        return false;
    }
}
