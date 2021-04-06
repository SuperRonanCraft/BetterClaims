package me.RonanCraft.Pueblos.events.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.events.command.PueblosCommand;
import me.RonanCraft.Pueblos.events.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CmdReload implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "reload";
    }


    public void execute(CommandSender sendi, String label, String[] args) {
        Pueblos.getInstance().reload(sendi);
    }

    public boolean permission(CommandSender sendi) {
        return Pueblos.getInstance().getPermissions().getReload(sendi);
    }

    @Override
    public String getHelp() {
        return Messages.help.getHelpReload();
    }
}
