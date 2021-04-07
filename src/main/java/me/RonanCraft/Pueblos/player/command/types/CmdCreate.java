package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimPosition;
import me.RonanCraft.Pueblos.resources.claims.ClaimEvents;
import me.RonanCraft.Pueblos.resources.claims.ClaimHandler;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import me.RonanCraft.Pueblos.resources.tools.packet.SendPacketBlock;
import me.RonanCraft.Pueblos.resources.tools.visual.Visualization;
import me.RonanCraft.Pueblos.resources.tools.visual.VisualizationType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CmdCreate implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "create";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;
        ClaimHandler handler = Pueblos.getInstance().getSystems().getClaimHandler();

        //Event
        Claim claim = handler.claimCreate(p.getUniqueId(), p.getName(), new ClaimPosition(p.getLocation().getWorld(),
                p.getLocation().clone().add(-8, 0, -8),
                p.getLocation().clone().add(8, 0, 8)));
        if (claim != null) {
            ClaimEvents.create(claim);
            Pueblos.getInstance().getSystems().getDatabase().createClaim(claim);
            Messages.core.sms(sendi, "&aClaim was created!");
            Visualization.fromClaim(claim, p.getLocation().getBlockY(), VisualizationType.Claim, p.getLocation()).apply(p);
        } else {
            Messages.core.sms(sendi, "&cClaim was NOT created!");
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
}
