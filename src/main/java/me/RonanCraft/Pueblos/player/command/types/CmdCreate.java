package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_TYPE;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesHelp;
import me.RonanCraft.Pueblos.resources.tools.HelperClaim;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdCreate implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "create";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;

        HelperClaim.registerClaim(p,
                p.getWorld(), p.getLocation().clone().add(-8, 0, -8),
                p.getLocation().clone().add(8, 0, 8), true, null, CLAIM_TYPE.MAIN);
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
