package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.inventory.PueblosInventory;
import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.claims.CLAIM_FLAG_MEMBER;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimHandler;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesHelp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.List;

public class CmdList implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "list";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        ClaimHandler handler = Pueblos.getInstance().getSystems().getClaimHandler();
        Player p = (Player) sendi;
        List<Claim> claims = handler.getClaims(p.getUniqueId());
        if (!claims.isEmpty()) {
            if (claims.size() == 1)
                PueblosInventory.CLAIM.open(p, claims.get(0), true);
            else
                PueblosInventory.CLAIM_SELECT.open(p, claims, true);
        } else
            MessagesCore.CLAIM_NONE.send(sendi);
    }

    public boolean permission(CommandSender sendi) {
        return PermissionNodes.USE.check(sendi);
    }

    @Override
    public String getHelp() {
        return MessagesHelp.LIST.get();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
