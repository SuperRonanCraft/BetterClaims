package me.RonanCraft.BetterClaims.player.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface ClaimCommandTabComplete {

    List<String> tabComplete(CommandSender sendi, String[] args);
}
