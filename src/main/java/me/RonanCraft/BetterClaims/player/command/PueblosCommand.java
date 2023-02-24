package me.RonanCraft.BetterClaims.player.command;

import me.RonanCraft.BetterClaims.BetterClaims;
import org.bukkit.command.CommandSender;

public interface PueblosCommand {

    void execute(CommandSender sendi, String label, String[] args);

    boolean permission(CommandSender sendi);

    String getName();

    default boolean isPlayerOnly() {
        return false;
    }

    default BetterClaims getPl() {
        return BetterClaims.getInstance();
    }
}
