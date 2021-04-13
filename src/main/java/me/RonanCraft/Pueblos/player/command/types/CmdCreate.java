package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.claims.*;
import me.RonanCraft.Pueblos.resources.files.msgs.Message;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesHelp;
import me.RonanCraft.Pueblos.resources.tools.HelperClaim;
import me.RonanCraft.Pueblos.resources.tools.visual.Visualization;
import me.RonanCraft.Pueblos.resources.tools.visual.VisualizationType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdCreate implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "create";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;

        HelperClaim.createClaim(p,
                p.getLocation().clone().add(-8, 0, -8),
                p.getLocation().clone().add(8, 0, 8), true);
    }

    public boolean permission(CommandSender sendi) {
        return PermissionNodes.USE.check(sendi);
    }

    @Override
    public String getHelp() {
        return MessagesHelp.CREATE.get();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
