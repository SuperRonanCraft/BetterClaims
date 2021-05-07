package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimHandler;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_ERRORS;
import me.RonanCraft.Pueblos.resources.claims.selling.Auction;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesHelp;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesUsage;
import me.RonanCraft.Pueblos.resources.tools.visual.Visualization;
import me.RonanCraft.Pueblos.resources.tools.visual.VisualizationType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdAuction implements PueblosCommand, PueblosCommandHelpable {

    //Auctions off a claim

    public String getName() {
        return "auction";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        ClaimHandler handler = Pueblos.getInstance().getClaimHandler();
        Player p = (Player) sendi;
        Claim claim = handler.getClaimAt(p.getLocation(), false);
        if (claim != null) {
            if (claim.isOwner(p)) {
                CLAIM_ERRORS error = handler.getAuctionManager().createAuction(claim, 0, 0);
                if (error == CLAIM_ERRORS.NONE) {
                    Auction auction = handler.getAuctionManager().getAuction(claim);
                    Visualization.fromClaim(claim, p.getLocation().getBlockY(), VisualizationType.CLAIM, p.getLocation()).apply(p);
                    MessagesCore.AUCTION_CREATED.send(sendi, auction);
                } else
                    error.sendMsg(sendi, claim);
            } else
                MessagesCore.AUCTION_NOTOWNER.send(sendi);
        } else {
            MessagesCore.AUCTION_NOTINSIDE.send(sendi);
        }
    }

    private void create(Player p, Claim claim, String label, String[] args) {
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
