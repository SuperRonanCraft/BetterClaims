package me.RonanCraft.Pueblos.events.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.events.command.PueblosCommand;
import me.RonanCraft.Pueblos.events.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimHandler;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import org.bukkit.command.CommandSender;

import java.util.*;

public class CmdInfo implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "info";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        List<String> info = new ArrayList<>();
        ClaimHandler handler = Pueblos.getInstance().getSystems().getClaimHandler();
        info.add("Claims: " + handler.getClaims().size());
        int chunks = 0;
        for (Map.Entry<UUID, Claim> entry : handler.getClaims()) {
            Claim claim = entry.getValue();
            chunks += claim.getChunks().size();
        }
        info.add("Chunks Protected: " + chunks);
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
