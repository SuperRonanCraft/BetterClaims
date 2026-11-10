package me.RonanCraft.BetterClaims.player.command.types;

import me.RonanCraft.BetterClaims.player.command.ClaimCommand;
import me.RonanCraft.BetterClaims.player.command.ClaimCommandHelpable;
import me.RonanCraft.BetterClaims.resources.messages.MessagesHelp;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdEditor implements ClaimCommand, ClaimCommandHelpable {
    public String getName() { return "editor"; }
    
    public boolean permission(CommandSender sender) { return !(sender instanceof Player) || sender.hasPermission("betterclaims.editor"); }
    
    public String getHelp() { return MessagesHelp.EDITOR.get(); }
    
    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length != 1) { sendi.sendMessage("§eUsage: /betterclaims editor"); return; }
        getPl().getEditor().upload(sendi);
    }
}
