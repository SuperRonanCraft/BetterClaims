package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.messages.MessagesHelp;
import org.bukkit.command.CommandSender;

public class CmdReload implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "reload";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Pueblos.getInstance().reload(sendi);
    }

    public boolean permission(CommandSender sendi) {
        return  PermissionNodes.RELOAD.check(sendi);
    }

    @Override
    public String getHelp() {
        return MessagesHelp.RELOAD.get();
    }
}
