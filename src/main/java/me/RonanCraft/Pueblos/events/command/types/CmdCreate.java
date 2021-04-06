package me.RonanCraft.Pueblos.events.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.events.command.PueblosCommand;
import me.RonanCraft.Pueblos.events.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimEvents;
import me.RonanCraft.Pueblos.resources.claims.ClaimHandler;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdCreate implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "create";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        if (!(sendi instanceof Player)) {
            console(sendi);
            return;
        }
        Player p = (Player) sendi;
        Claim claim;
        ClaimHandler handler = Pueblos.getInstance().getSystems().getClaimHandler();

        if (handler.getClaim(p.getUniqueId()) == null)
            claim = new Claim(p.getUniqueId(), p.getName(), p.getWorld());
        else
            claim = handler.getClaim(p.getUniqueId());

        //Add chunks
        claim.addChunk(p.getLocation().getChunk());

        //Event
        if (handler.getClaim(p.getUniqueId()) == null) {
            ClaimEvents.create(claim);
            handler.addClaim(claim);
        }
        Messages.core.sms(sendi, "&aChunk " + p.getLocation().getChunk().toString() + " was created!");
    }

    private void console(CommandSender sendi) {
        ClaimHandler handler = Pueblos.getInstance().getSystems().getClaimHandler();
        World world = Bukkit.getWorlds().get(0);
        Claim claim = new Claim(null, "CONSOLE", world);
        handler.addClaim(claim);
        claim.addChunk(world.getChunkAt(0, 0));
        claim.addChunk(world.getChunkAt(0, 1));
        sendi.sendMessage("Console added chunk at 0,0 and 0,1");
        Pueblos.getInstance().getSystems().getDatabase().createClaim(claim);
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
