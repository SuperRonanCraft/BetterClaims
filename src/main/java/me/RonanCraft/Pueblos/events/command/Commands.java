package me.RonanCraft.Pueblos.events.command;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.files.FileOther;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Commands {

    public List<PueblosCommand> commands = new ArrayList<>();

    public void load() {
        commands.clear();
        for (PueblosCommandType cmd : PueblosCommandType.values())
           registerCommand(cmd.getCmd());
    }

    public void registerCommand(PueblosCommand cmd) {
        //if (!cmd.isPlayerOnly()) //If debug only, can it be enabled?
        commands.add(cmd);
    }

    public void commandExecuted(CommandSender sendi, String label, String[] args) {
        if (getPl().getPermissions().getUse(sendi)) {
            if (args != null && args.length > 0) {
                for (PueblosCommand cmd : commands) {
                    if (cmd.getName().equalsIgnoreCase(args[0])) {
                        if (cmd.isPlayerOnly() && !(sendi instanceof Player))
                            Messages.core.sms(sendi, "Console is not allowed to run this command!");
                        /*else */if (cmd.permission(sendi)) {
                            cmd.execute(sendi, label, args);
                        } else
                            noPerm(sendi);
                        return;
                    }
                }
                invalid(sendi, label);
            } else
                PueblosCommandType.HELP.getCmd().execute(sendi, label, args);
        } else
            noPerm(sendi);
    }

    private void invalid(CommandSender sendi, String cmd) {
        Messages.core.sendInvalidCommand(sendi);
    }

    private void noPerm(CommandSender sendi) {
        Messages.core.sendNoPermission(sendi);
    }

    public List<String> onTabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            for (PueblosCommand cmd : commands) {
                if (cmd.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    if (cmd.permission(sendi))
                        list.add(cmd.getName().toLowerCase());
            }
        } else if (args.length > 1) {
            for (PueblosCommand cmd : commands) {
                if (cmd.getName().equalsIgnoreCase(args[0]))
                    if (cmd.permission(sendi)) {
                        List<String> _cmdlist = cmd.tabComplete(sendi, args);
                        if (_cmdlist != null)
                            list.addAll(_cmdlist);
                    }
            }
        }
        return list;
    }

    private Pueblos getPl() {
        return Pueblos.getInstance();
    }
}
