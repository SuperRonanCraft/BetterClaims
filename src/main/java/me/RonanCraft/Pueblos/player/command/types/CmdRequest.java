package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.inventory.PueblosInventory;
import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.player.command.PueblosCommandTabComplete;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_FLAG;
import me.RonanCraft.Pueblos.resources.claims.ClaimMain;
import me.RonanCraft.Pueblos.resources.files.msgs.Message;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesHelp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdRequest implements PueblosCommand, PueblosCommandHelpable, PueblosCommandTabComplete {

    public String getName() {
        return "request";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;
        List<ClaimMain> requestable = getRequestable(p);
        if (!requestable.isEmpty()) {
            PueblosInventory.REQUESTING.open(p, requestable, true);
        } else
            Message.sms(p, "No claims to join!", null);
    }

    public static List<ClaimMain> getRequestable(Player p) { //Get all claims a player can request to be in
        List<ClaimMain> claims = new ArrayList<>();
        for (ClaimMain claim : Pueblos.getInstance().getClaimHandler().getClaimsMain())
            //Not the owner or member and claim is accepting requests
            if (    !claim.isAdminClaim() //Not an admin Claim
                    && claim.getOwnerID() != null //Owners UUID isnt trash
                    && !claim.isOwner(p) //Not an owner of this claim
                    && !claim.isMember(p) //Is not already a member
                    && (Boolean) claim.getFlags().getFlag(CLAIM_FLAG.ALLOW_REQUESTS)) { //Claim is accepting requests
                claims.add(claim);
            }
        return claims;
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
