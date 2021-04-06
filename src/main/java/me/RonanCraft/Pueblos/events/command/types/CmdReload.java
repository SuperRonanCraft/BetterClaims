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

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();

        return list;
    }

    public boolean permission(CommandSender sendi) {
        return Pueblos.getInstance().getPermissions().getReload(sendi);
    }

    public void usage(CommandSender sendi, String label) {
        //Pueblos.getInstance().getMessages().getUsageWorld(sendi, label);
    }

    @Override
    public String getHelp() {
        return Messages.help.getHelpReload();
    }
}
