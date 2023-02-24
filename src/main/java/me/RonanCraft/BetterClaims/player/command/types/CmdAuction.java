package me.RonanCraft.BetterClaims.player.command.types;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.player.command.ClaimCommand;
import me.RonanCraft.BetterClaims.player.command.ClaimCommandHelpable;
import me.RonanCraft.BetterClaims.resources.PermissionNodes;
import me.RonanCraft.BetterClaims.claims.ClaimData;
import me.RonanCraft.BetterClaims.claims.ClaimHandler;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_ERRORS;
import me.RonanCraft.BetterClaims.auction.Auction;
import me.RonanCraft.BetterClaims.resources.messages.MessagesCore;
import me.RonanCraft.BetterClaims.resources.messages.MessagesHelp;
import me.RonanCraft.BetterClaims.resources.messages.MessagesUsage;
import me.RonanCraft.BetterClaims.resources.visualization.Visualization;
import me.RonanCraft.BetterClaims.resources.visualization.VisualizationType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdAuction implements ClaimCommand, ClaimCommandHelpable {

    //Auctions off a claim

    public String getName() {
        return "auction";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        ClaimHandler handler = BetterClaims.getInstance().getClaimHandler();
        Player p = (Player) sendi;
        ClaimData claimData = handler.getClaimAt(p.getLocation(), false);
        if (claimData != null) {
            if (claimData.isOwner(p)) {
                CLAIM_ERRORS error = handler.getAuctionManager().createAuction(claimData, 0, 0);
                if (error == CLAIM_ERRORS.NONE) {
                    Auction auction = handler.getAuctionManager().getAuction(claimData);
                    Visualization.fromClaim(claimData, p.getLocation().getBlockY(), VisualizationType.CLAIM, p.getLocation()).apply(p);
                    MessagesCore.AUCTION_CREATED.send(sendi, auction);
                } else
                    error.sendMsg(sendi, claimData);
            } else
                MessagesCore.AUCTION_NOTOWNER.send(sendi);
        } else {
            MessagesCore.AUCTION_NOTINSIDE.send(sendi);
        }
    }

    private void create(Player p, ClaimData claimData, String label, String[] args) {
        MessagesUsage.AUCTION_CREATE.send(p, label);
    }

    public boolean permission(CommandSender sendi) {
        return PermissionNodes.USE.check(sendi);
    }

    @Override
    public String getHelp() {
        return MessagesHelp.AUCTION.get();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    private enum SUB_CMDS {
        CREATE, DELETE, SIGN
    }
}
