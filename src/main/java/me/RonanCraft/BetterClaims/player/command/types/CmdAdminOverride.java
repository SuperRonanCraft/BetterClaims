package me.RonanCraft.BetterClaims.player.command.types;

import me.RonanCraft.BetterClaims.player.command.ClaimCommand;
import me.RonanCraft.BetterClaims.player.command.ClaimCommandHelpable;
import me.RonanCraft.BetterClaims.player.data.PlayerData;
import me.RonanCraft.BetterClaims.resources.PermissionNodes;
import me.RonanCraft.BetterClaims.resources.helper.HelperPlayer;
import me.RonanCraft.BetterClaims.resources.messages.MessagesCore;
import me.RonanCraft.BetterClaims.resources.messages.MessagesHelp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdAdminOverride implements ClaimCommand, ClaimCommandHelpable {

    public String getName() {
        return "adminoverride";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;
        PlayerData data = HelperPlayer.getData(p);
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
