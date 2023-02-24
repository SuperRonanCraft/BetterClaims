package me.RonanCraft.BetterClaims.player.command.types;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.inventory.PueblosInventory;
import me.RonanCraft.BetterClaims.player.command.PueblosCommand;
import me.RonanCraft.BetterClaims.player.command.PueblosCommandHelpable;
import me.RonanCraft.BetterClaims.resources.PermissionNodes;
import me.RonanCraft.BetterClaims.claims.*;
import me.RonanCraft.BetterClaims.claims.Claim;
import me.RonanCraft.BetterClaims.claims.ClaimHandler;
import me.RonanCraft.BetterClaims.resources.messages.MessagesCore;
import me.RonanCraft.BetterClaims.resources.messages.MessagesHelp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class CmdInfo implements PueblosCommand, PueblosCommandHelpable {

    //Opens a GUI to view claim information

    public String getName() {
        return "info";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        ClaimHandler handler = BetterClaims.getInstance().getClaimHandler();
        Player p = (Player) sendi;
        Claim claim = handler.getClaimMain(p.getLocation());
        if (claim != null) {
            if (claim.isMember(p)) {
                PueblosInventory.CLAIM.open(p, claim, true);
            } else {
                if (claim.isAdminClaim()) {
                    if (PermissionNodes.ADMIN_CLAIM.check(sendi))
                        PueblosInventory.CLAIM.open(p, claim, true);
                    else
                        MessagesCore.CLAIM_PERMISSION_ADMINCLAIM.send(sendi, claim);
                } else
                    MessagesCore.CLAIM_PERMISSION_CLAIM.send(sendi, claim);
            }
        } else {
            List<ClaimData> claimData = handler.getClaims(p.getUniqueId());
            if (!claimData.isEmpty()) {
                if (claimData.size() == 1)
                    PueblosInventory.CLAIM.open(p, claimData.get(0), true);
                else
                    PueblosInventory.CLAIM_SELECT.open(p, claimData, true);
            } else
                MessagesCore.CLAIM_NONE.send(sendi);
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
