package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimHandler;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class CmdInfo implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "info";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        List<String> info = new ArrayList<>();
        ClaimHandler handler = Pueblos.getInstance().getSystems().getClaimHandler();
        info.add("Claims: " + handler.getClaims().size());
        if (sendi instanceof Player) {
            Player p = (Player) sendi;
            //List<Claim> claim = handler.getClaims(p.getUniqueId());
            //CmdCreate.showCorners(p, claim);
        }
        Messages.core.sms(sendi, info);
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
}
