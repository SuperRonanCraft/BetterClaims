package me.RonanCraft.BetterClaims.player.command.types;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.inventory.ClaimInventory;
import me.RonanCraft.BetterClaims.player.command.ClaimCommand;
import me.RonanCraft.BetterClaims.player.command.ClaimCommandHelpable;
import me.RonanCraft.BetterClaims.player.command.ClaimCommandTabComplete;
import me.RonanCraft.BetterClaims.resources.PermissionNodes;
import me.RonanCraft.BetterClaims.claims.ClaimData;
import me.RonanCraft.BetterClaims.claims.ClaimHandler;
import me.RonanCraft.BetterClaims.resources.helper.HelperClaim;
import me.RonanCraft.BetterClaims.resources.messages.MessagesCore;
import me.RonanCraft.BetterClaims.resources.messages.MessagesHelp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdList implements ClaimCommand, ClaimCommandHelpable, ClaimCommandTabComplete {

    public String getName() {
        return "list";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;
        boolean admin = args.length > 1 && args[1].equalsIgnoreCase("admin") && PermissionNodes.ADMIN_CLAIM.check(sendi);
        List<ClaimData> claimData = HelperClaim.getHandler().getClaims(p.getUniqueId(), admin);
        if (!claimData.isEmpty()) {
            if (claimData.size() == 1)
                ClaimInventory.CLAIM.open(p, claimData.get(0), true);
            else
                ClaimInventory.CLAIM_SELECT.open(p, claimData, true);
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

    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2 && ("admin").startsWith(args[1]) && PermissionNodes.ADMIN_CLAIM.check(sendi))
            list.add("admin");
        return list;
    }
}
