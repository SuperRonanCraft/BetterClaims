package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesHelp;
import me.RonanCraft.Pueblos.resources.tools.HelperClaim;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdAdminclaim implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "adminclaim";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;
        getPl().getSystems().getClaimHandler().toggleAdminClaimMode(p.getUniqueId());
    }

    public boolean permission(CommandSender sendi) {
        return PermissionNodes.ADMIN_CLAIM.check(sendi);
    }

    @Override
    public String getHelp() {
        return MessagesHelp.CREATE.get();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
