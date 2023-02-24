package me.RonanCraft.BetterClaims.player.command.types;

import me.RonanCraft.BetterClaims.player.command.ClaimCommand;
import me.RonanCraft.BetterClaims.player.command.ClaimCommandHelpable;
import me.RonanCraft.BetterClaims.resources.PermissionNodes;
import me.RonanCraft.BetterClaims.resources.messages.MessagesHelp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdAdminClaim implements ClaimCommand, ClaimCommandHelpable {

    public String getName() {
        return "adminclaim";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;
        getPl().getEvents().toggleAdminClaim(p);
    }

    public boolean permission(CommandSender sendi) {
        return PermissionNodes.ADMIN_CLAIM.check(sendi);
    }

    @Override
    public String getHelp() {
        return MessagesHelp.ADMIN_CLAIM.get();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
