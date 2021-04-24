package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesHelp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdAdminOverride implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "adminoverride";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;
        getPl().getSystems().getPlayerInfo().addOverride(p);
    }

    public boolean permission(CommandSender sendi) {
        return PermissionNodes.ADMIN_OVERRIDE.check(sendi);
    }

    @Override
    public String getHelp() {
        return MessagesHelp.ADMIN_OVERRIDE.get();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
