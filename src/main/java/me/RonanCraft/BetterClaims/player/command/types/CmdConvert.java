package me.RonanCraft.BetterClaims.player.command.types;

import me.RonanCraft.BetterClaims.player.command.ClaimCommand;
import me.RonanCraft.BetterClaims.player.command.ClaimCommandEnableable;
import me.RonanCraft.BetterClaims.player.command.ClaimCommandHelpable;
import me.RonanCraft.BetterClaims.player.command.ClaimCommandTabComplete;
import me.RonanCraft.BetterClaims.resources.PermissionNodes;
import me.RonanCraft.BetterClaims.resources.dependencies.ConverterGriefPrevention;
import me.RonanCraft.BetterClaims.resources.messages.MessagesCore;
import me.RonanCraft.BetterClaims.resources.messages.MessagesHelp;
import me.RonanCraft.BetterClaims.resources.messages.MessagesUsage;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CmdConvert implements ClaimCommand, ClaimCommandHelpable, ClaimCommandEnableable, ClaimCommandTabComplete {

    public String getName() {
        return "convert";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length >= 2) {
            try {
                CONVERTIONS con = CONVERTIONS.valueOf(args[1].toUpperCase());
                switch (con) {
                    case GRIEFPREVENTION:
                        new ConverterGriefPrevention().load(sendi); break;
                    default:
                        sendi.sendMessage("Not yet finished/implemented!");
                }
            } catch (IllegalArgumentException e) {
                MessagesCore.CONVERT_UNKNOWN.send(sendi);
            }
        } else
            MessagesUsage.CONVERT.send(sendi, label);
    }

    public boolean permission(CommandSender sendi) {
        return PermissionNodes.ADMIN_CONVERT.check(sendi);
    }

    @Override
    public boolean isEnabled() {
        return ConverterGriefPrevention.pathExist();
    }

    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        for (CONVERTIONS con : CONVERTIONS.values())
            list.add(con.name().toLowerCase());
        return list;
    }

    @Override
    public String getHelp() {
        return MessagesHelp.CONVERT.get();
    }

    private enum CONVERTIONS {
        GRIEFPREVENTION
    }
}
