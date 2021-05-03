package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.player.data.PlayerData;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesHelp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdAdminOverride implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "adminoverride";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;
        PlayerData data = getPl().getPlayerData(p);
        data.toggleOverride();
        if (data.isOverriding()) { //Send message override is enabled
            MessagesCore.CLAIM_OVERRIDE_ENABLED.send(sendi);
        } else { //Send message override is disabled
            MessagesCore.CLAIM_OVERRIDE_DISABLED.send(sendi);
        }
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
