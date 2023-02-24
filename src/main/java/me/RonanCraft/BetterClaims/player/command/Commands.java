package me.RonanCraft.BetterClaims.player.command;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.resources.PermissionNodes;
import me.RonanCraft.BetterClaims.resources.messages.Message;
import me.RonanCraft.BetterClaims.resources.messages.MessagesCore;
import me.RonanCraft.BetterClaims.resources.helper.HelperEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Commands {

    public List<ClaimCommand> commands = new ArrayList<>();

    public void load() {
        commands.clear();
        for (ClaimCommandType cmd : ClaimCommandType.values())
           registerCommand(cmd.getCmd());
    }

    public void registerCommand(ClaimCommand cmd) {
        if (cmd instanceof ClaimCommandEnableable) //If can be disabled, check if enabled
            if (!((ClaimCommandEnableable) cmd).isEnabled())
                return;
        commands.add(cmd);
    }

    public void commandExecuted(CommandSender sendi, String label, String[] args) {
        if (PermissionNodes.USE.check(sendi)) {
            if (args != null && args.length > 0) {
                for (ClaimCommand cmd : commands) {
                    if (cmd.getName().equalsIgnoreCase(args[0])) {
                        if (cmd.isPlayerOnly() && !(sendi instanceof Player))
                            Message.sms(sendi, "Console is not allowed to run this command!", null);
                        else if (cmd.permission(sendi)) {
                            cmd.execute(sendi, label, args);
                            HelperEvent.command(sendi, cmd);
                        } else
                            noPerm(sendi);
                        return; 
                    }
                }
                invalid(sendi, label);
            } else
                ClaimCommandType.HELP.getCmd().execute(sendi, label, args);
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
            for (ClaimCommand cmd : commands) {
                if (cmd.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    if (cmd.permission(sendi))
                        list.add(cmd.getName().toLowerCase());
            }
        } else if (args.length > 1) {
            for (ClaimCommand cmd : commands) {
                if (cmd.getName().equalsIgnoreCase(args[0]) && cmd instanceof ClaimCommandTabComplete)
                    if (cmd.permission(sendi)) {
                        List<String> _cmdlist = ((ClaimCommandTabComplete) cmd).tabComplete(sendi, args);
                        if (_cmdlist != null)
                            list.addAll(_cmdlist);
                    }
            }
        }
        return list;
    }

    private BetterClaims getPl() {
        return BetterClaims.getInstance();
    }
}
