package me.RonanCraft.BetterClaims.player.command.types;

import me.RonanCraft.BetterClaims.player.command.PueblosCommand;
import me.RonanCraft.BetterClaims.player.command.PueblosCommandHelpable;
import me.RonanCraft.BetterClaims.resources.PermissionNodes;
import me.RonanCraft.BetterClaims.resources.messages.MessagesHelp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdClaimItem implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "claimitem";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;
        getPl().getEvents().toggleAdminClaim(p);
    }

    public boolean permission(CommandSender sendi) {
        return PermissionNodes.CLAIM_ITEM.check(sendi);
    }

    @Override
    public String getHelp() {
        return MessagesHelp.CLAIM_ITEM.get();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
