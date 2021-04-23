package me.RonanCraft.Pueblos.player.command;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.Permissions;
import me.RonanCraft.Pueblos.resources.files.msgs.Message;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
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
        if (cmd instanceof PueblosCommandEnableable) //If can be disabled, check if enabled
            if (!((PueblosCommandEnableable) cmd).isEnabled())
                return;
        commands.add(cmd);
    }

    public void commandExecuted(CommandSender sendi, String label, String[] args) {
        if (PermissionNodes.USE.check(sendi)) {
            if (args != null && args.length > 0) {
                for (PueblosCommand cmd : commands) {
                    if (cmd.getName().equalsIgnoreCase(args[0])) {
                        if (cmd.isPlayerOnly() && !(sendi instanceof Player))
                            Message.sms(sendi, "Console is not allowed to run this command!", null);
                        else if (cmd.permission(sendi)) {
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
        MessagesCore.INVALIDCOMMAND.send(sendi, cmd);
    }

    private void noPerm(CommandSender sendi) {
        MessagesCore.NOPERMISSION.send(sendi);
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
                if (cmd.getName().equalsIgnoreCase(args[0]) && cmd instanceof PueblosCommandTabComplete)
                    if (cmd.permission(sendi)) {
                        List<String> _cmdlist = ((PueblosCommandTabComplete) cmd).tabComplete(sendi, args);
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
