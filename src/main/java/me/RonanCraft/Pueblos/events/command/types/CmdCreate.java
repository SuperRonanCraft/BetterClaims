package me.RonanCraft.Pueblos.events.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.events.command.PueblosCommand;
import me.RonanCraft.Pueblos.events.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimPosition;
import me.RonanCraft.Pueblos.resources.claims.ClaimEvents;
import me.RonanCraft.Pueblos.resources.claims.ClaimHandler;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdCreate implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "create";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;
        Claim claim;
        ClaimHandler handler = Pueblos.getInstance().getSystems().getClaimHandler();

        if (handler.getClaim(p.getUniqueId()) == null)
            claim = new Claim(p.getUniqueId(), p.getName(), p.getWorld());
        else
            claim = handler.getClaim(p.getUniqueId());

        //Add chunks
        claim.setPosition(new ClaimPosition(p.getLocation(), p.getLocation().clone().add(16, 0, 16)));

        //Event
        if (handler.getClaim(p.getUniqueId()) == null) {
            ClaimEvents.create(claim);
            handler.addClaim(claim);
        }
        Messages.core.sms(sendi, "&aChunk " + p.getLocation().getChunk().toString() + " was created!");
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
