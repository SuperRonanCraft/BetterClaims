package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.messages.MessagesHelp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdAdminClaim implements PueblosCommand, PueblosCommandHelpable {

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
