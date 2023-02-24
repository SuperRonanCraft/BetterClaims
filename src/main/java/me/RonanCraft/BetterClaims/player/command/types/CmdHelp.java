package me.RonanCraft.BetterClaims.player.command.types;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.player.command.PueblosCommand;
import me.RonanCraft.BetterClaims.player.command.PueblosCommandHelpable;
import me.RonanCraft.BetterClaims.resources.messages.Message;
import me.RonanCraft.BetterClaims.resources.messages.MessagesHelp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdHelp implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "help";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        if (!(sendi instanceof Player)) //Console warning message
            sendi.sendMessage("Console might not be able to execute some of these commands!");
        List<String> list = new ArrayList<>();
        list.add(MessagesHelp.PREFIX.get());
        for (PueblosCommand cmd : BetterClaims.getInstance().getCmd().commands)
            if (cmd.permission(sendi))
                if (cmd instanceof PueblosCommandHelpable) {
                    String help = ((PueblosCommandHelpable) cmd).getHelp();
                    list.add(help);
                }
        for (int i = 0; i < list.size(); i++)
            list.set(i, list.get(i).replace("%command%", label));
        Message.sms(sendi, list, null);
    }

    public boolean permission(CommandSender sendi) {
        return true;
    }

    @Override
    public String getHelp() {
        return MessagesHelp.HELP.get();
    }
}
