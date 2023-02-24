package me.RonanCraft.BetterClaims.player.command.types;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.claims.Claim;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_PERMISSION_LEVEL;
import me.RonanCraft.BetterClaims.inventory.PueblosInventory;
import me.RonanCraft.BetterClaims.player.command.PueblosCommand;
import me.RonanCraft.BetterClaims.player.command.PueblosCommandHelpable;
import me.RonanCraft.BetterClaims.player.command.PueblosCommandTabComplete;
import me.RonanCraft.BetterClaims.resources.PermissionNodes;
import me.RonanCraft.BetterClaims.claims.ClaimData;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_FLAG;
import me.RonanCraft.BetterClaims.resources.messages.Message;
import me.RonanCraft.BetterClaims.resources.messages.MessagesHelp;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CmdRequest implements PueblosCommand, PueblosCommandHelpable, PueblosCommandTabComplete {

    public String getName() {
        return "request";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;
        List<ClaimData> requestable = getRequestable(p, args.length >= 2 ? Bukkit.getPlayer(args[1]) : null);
        if (!requestable.isEmpty()) {
            PueblosInventory.REQUESTING_ALL.open(p, requestable, true);
        } else
            Message.sms(p, "No claims to join!", null);
    }

    public static List<ClaimData> getRequestable(Player p, @Nullable Player target) { //Get all claims a player can request to be in
        List<ClaimData> requestable = new ArrayList<>();
        if (target == null) {
            for (Claim claim : BetterClaims.getInstance().getClaimHandler().getClaimsMain())
                //Not the owner or member and claim is accepting requests
                if (!claim.isAdminClaim() //Not an admin Claim
                        && claim.getOwnerID() != null //Owners UUID isnt trash
                        && !claim.isOwner(p) //Not an owner of this claim
                        && !claim.isMember(p) //Is not already a member
                        && (Boolean) claim.getFlags().getFlag(CLAIM_FLAG.ALLOW_REQUESTS)) { //Claim is accepting requests
                    requestable.add(claim);
                }
        } else {
            for (ClaimData claim : BetterClaims.getInstance().getClaimHandler().getClaims(target.getUniqueId())) {
                if (claim.checkPermLevel(target, CLAIM_PERMISSION_LEVEL.OWNER))
                    requestable.add(claim);
            }
        }
        return requestable;
    }

    public boolean permission(CommandSender sendi) {
        return  PermissionNodes.REQUEST.check(sendi);
    }

    @Override
    public String getHelp() {
        return MessagesHelp.REQUEST.get();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2)
            for (CLAIM_FLAG flag : CLAIM_FLAG.values())
                if (flag.name().startsWith(args[1].toUpperCase()))
                    list.add(flag.name().toLowerCase());
        return list;
    }
}
