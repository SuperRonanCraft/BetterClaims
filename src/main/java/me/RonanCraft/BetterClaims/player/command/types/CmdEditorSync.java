package me.RonanCraft.BetterClaims.player.command.types;

import me.RonanCraft.BetterClaims.player.command.ClaimCommand;
import me.RonanCraft.BetterClaims.player.command.ClaimCommandHelpable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdEditorSync implements ClaimCommand, ClaimCommandHelpable {
    public String getName() { return "editorsync"; }
    public boolean permission(CommandSender sender) { return !(sender instanceof Player) || sender.hasPermission("betterclaims.editor"); }
    public String getHelp() { return "/betterclaims editorsync <token>"; }
    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length != 2) { sendi.sendMessage("§eUsage: /betterclaims editorsync <token>"); return; }
        getPl().getEditor().redeem(sendi, args[1]);
    }
}
