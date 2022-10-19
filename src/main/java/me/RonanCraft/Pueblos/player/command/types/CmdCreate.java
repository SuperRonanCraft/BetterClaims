package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.claims.enums.CLAIM_ERRORS;
import me.RonanCraft.Pueblos.claims.enums.CLAIM_TYPE;
import me.RonanCraft.Pueblos.resources.messages.MessagesHelp;
import me.RonanCraft.Pueblos.resources.helper.HelperClaim;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdCreate implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "create";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;

        Bukkit.getScheduler().runTaskAsynchronously(getPl(), () -> {
            CLAIM_ERRORS error = HelperClaim.registerClaim(p,
                    p.getWorld(), p.getLocation().clone().add(-7, 0, -8),
                    p.getLocation().clone().add(8, 0, 8), null, CLAIM_TYPE.PARENT);
            error.sendMsg(sendi, null);
        });
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
