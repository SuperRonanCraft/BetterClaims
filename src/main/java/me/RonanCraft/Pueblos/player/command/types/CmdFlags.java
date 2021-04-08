package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.player.command.PueblosCommandTabComplete;
import me.RonanCraft.Pueblos.resources.claims.*;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdFlags implements PueblosCommand, PueblosCommandHelpable, PueblosCommandTabComplete {

    public String getName() {
        return "flags";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;
        ClaimHandler handler = Pueblos.getInstance().getSystems().getClaimHandler();

        //Event
        Claim claim = handler.getClaim(p.getLocation());
        if (claim != null && claim.isMember(p)) {
            if (args.length == 3) {
                for (CLAIM_FLAG flag : CLAIM_FLAG.values())
                    if (flag.name().equalsIgnoreCase(args[1])) {
                        try {
                            Object value = flag.cast(args[2]);
                            if (value == null)
                                throw new Exception();
                            claim.getFlags().setFlag(flag, value);
                            Messages.core.sms(p, "Set " + flag.name().toLowerCase() + " to " + value);
                        } catch (Exception e) {
                            Messages.core.sms(p, "Invalid value!");
                        }
                    }
            } else {
                Messages.core.sms(p, "Usage: /claim flag FLAG_TYPE VALUE");
            }
        } else {
            Messages.core.sms(p, "&cThis is not your claim!");
        }


    }

    public boolean permission(CommandSender sendi) {
        return true;
    }

    @Override
    public String getHelp() {
        return Messages.help.getHelpCreate();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2)
            for (CLAIM_FLAG flag : CLAIM_FLAG.values())
                if (flag.name().startsWith(args[1].toUpperCase()))
                    list.add(flag.name().toLowerCase());
        return list;
    }
}
