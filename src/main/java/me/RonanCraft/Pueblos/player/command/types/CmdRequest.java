package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.inventory.PueblosInventory;
import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.player.command.PueblosCommandTabComplete;
import me.RonanCraft.Pueblos.resources.claims.CLAIM_FLAG;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimHandler;
import me.RonanCraft.Pueblos.resources.claims.ClaimRequest;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
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
        List<Claim> requestable = getRequestable(p);
        if (!requestable.isEmpty()) {
            Messages.core.sms(p, "You can join " + requestable.size() + " claims!");
            PueblosInventory.REQUESTING.open(p, requestable, true);
        } else
            Messages.core.sms(p, "No claims to join!");
    }

    public static List<Claim> getRequestable(Player p) { //Get all claims a player can request to be in
        List<Claim> claims = new ArrayList<>();
        for (Claim claim : Pueblos.getInstance().getSystems().getClaimHandler().getClaims())
            //Not the owner or member and claim is accepting requests
            if (!claim.isOwner(p) && !claim.isMember(p) && (Boolean) claim.getFlags().getFlag(CLAIM_FLAG.ALLOW_REQUESTS)) {
                claims.add(claim);
            }
        return claims;
    }

    public boolean permission(CommandSender sendi) {
        return true;
    }

    @Override
    public String getHelp() {
        return Messages.help.getHelpCreate();
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
