package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.inventory.PueblosInventory;
import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.claims.*;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesHelp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class CmdInfo implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "info";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        ClaimHandler handler = Pueblos.getInstance().getSystems().getClaimHandler();
        if (sendi instanceof Player) {
            Player p = (Player) sendi;
            Claim claim = handler.getClaim(p.getLocation());
            if (claim != null) {
                if (claim.isMember(p)) {
                    //---- JUNK CLAIM MEMBER
                    if (claim.getMembers().size() == 0) {
                        ClaimMember member = new ClaimMember(p.getUniqueId(), p.getName(), Calendar.getInstance().getTime(), false, claim);
                        member.setFlag(CLAIM_FLAG_MEMBER.ALLOW_BED, true, true);
                        claim.addMember(member, true);
                        Pueblos.getInstance().getSystems().getClaimDatabase().updateMembers(claim);
                    }
                    //----
                    PueblosInventory.CLAIM.open(p, claim, true);
                } else {
                    MessagesCore.CLAIM_NOPERMISSION.send(sendi, claim);
                }
            } else {
                List<Claim> claims = handler.getClaims(p.getUniqueId());
                if (!claims.isEmpty()) {
                    if (claims.size() == 1)
                        PueblosInventory.CLAIM.open(p, claims.get(0), true);
                    else
                        PueblosInventory.CLAIM_SELECT.open(p, claims, true);
                } else
                    MessagesCore.CLAIM_NONE.send(sendi);
            }
        }
    }

    public boolean permission(CommandSender sendi) {
        return PermissionNodes.USE.check(sendi);
    }

    @Override
    public String getHelp() {
        return MessagesHelp.INFO.get();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
