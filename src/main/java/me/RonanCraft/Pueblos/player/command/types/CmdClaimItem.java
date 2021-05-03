package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesHelp;
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
