package me.RonanCraft.BetterClaims.player.command.types;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.player.command.PueblosCommand;
import me.RonanCraft.BetterClaims.player.command.PueblosCommandHelpable;
import me.RonanCraft.BetterClaims.resources.PermissionNodes;
import me.RonanCraft.BetterClaims.resources.messages.MessagesHelp;
import org.bukkit.command.CommandSender;

public class CmdReload implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "reload";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        BetterClaims.getInstance().reload(sendi);
    }

    public boolean permission(CommandSender sendi) {
        return  PermissionNodes.RELOAD.check(sendi);
    }

    @Override
    public String getHelp() {
        return MessagesHelp.RELOAD.get();
    }
}
