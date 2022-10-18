package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandEnableable;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.player.command.PueblosCommandTabComplete;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.dependencies.ConverterGriefPrevention;
import me.RonanCraft.Pueblos.resources.messages.MessagesCore;
import me.RonanCraft.Pueblos.resources.messages.MessagesHelp;
import me.RonanCraft.Pueblos.resources.messages.MessagesUsage;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CmdConvert implements PueblosCommand,PueblosCommandHelpable, PueblosCommandEnableable, PueblosCommandTabComplete {

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
