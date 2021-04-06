package me.RonanCraft.Pueblos.events.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.events.command.PueblosCommand;
import me.RonanCraft.Pueblos.events.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesHelp;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CmdHelp implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "help";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        MessagesHelp txt = Messages.help;
        List<String> list = new ArrayList<>();
        list.add(txt.getHelpPrefix());
        for (PueblosCommand cmd : Pueblos.getInstance().getCmd().commands)
            if (cmd.permission(sendi))
                if (cmd instanceof PueblosCommandHelpable) {
                    String help = ((PueblosCommandHelpable) cmd).getHelp();
                    list.add(help);
                }
        for (int i = 0; i < list.size(); i++)
            list.set(i, list.get(i).replace("%command%", label));
        Messages.core.sms(sendi, list);
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }


    public boolean permission(CommandSender sendi) {
        return true;
    }

    @Override
    public String getHelp() {
        return Messages.help.getHelpHelp();
    }
}
