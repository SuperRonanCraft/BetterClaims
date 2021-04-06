package me.RonanCraft.Pueblos.events.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.events.command.PueblosCommand;
import me.RonanCraft.Pueblos.events.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimHandler;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdCreate implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "create";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;
        Claim claim;
        ClaimHandler handler = Pueblos.getInstance().getSystems().getClaimHandler();

        if (handler.getClaim(p.getUniqueId()) == null) {
            claim = new Claim(p.getUniqueId(), p.getName());
            handler.addClaim(claim);
        } else
            claim = handler.getClaim(p.getUniqueId());
        claim.addChunk(p.getLocation().getChunk());
        Messages.core.sms(sendi, "&aChunk " + p.getLocation().getChunk().toString() + " was added!");
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();

        return list;
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
