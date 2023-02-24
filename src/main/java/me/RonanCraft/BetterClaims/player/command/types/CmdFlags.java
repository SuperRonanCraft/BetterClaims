package me.RonanCraft.BetterClaims.player.command.types;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.player.command.ClaimCommand;
import me.RonanCraft.BetterClaims.player.command.ClaimCommandHelpable;
import me.RonanCraft.BetterClaims.player.command.ClaimCommandTabComplete;
import me.RonanCraft.BetterClaims.resources.PermissionNodes;
import me.RonanCraft.BetterClaims.claims.Claim;
import me.RonanCraft.BetterClaims.claims.ClaimHandler;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_FLAG;
import me.RonanCraft.BetterClaims.resources.messages.Message;
import me.RonanCraft.BetterClaims.resources.messages.MessagesCore;
import me.RonanCraft.BetterClaims.resources.messages.MessagesHelp;
import me.RonanCraft.BetterClaims.resources.helper.HelperClaim;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdFlags implements ClaimCommand, ClaimCommandHelpable, ClaimCommandTabComplete {

    public String getName() {
        return "flags";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;
        ClaimHandler handler = BetterClaims.getInstance().getClaimHandler();

        //Event
        if (args.length == 3) {
            CLAIM_FLAG flag = null;
            Object value = null;
            for (CLAIM_FLAG _flag : CLAIM_FLAG.values())
                if (_flag.name().equalsIgnoreCase(args[1])) {
                    try {
                        value = _flag.cast(args[2]);
                        if (value == null)
                            throw new Exception();
                        flag = _flag;
                    } catch (Exception e) {
                        MessagesCore.INVALIDFLAGVALUE.send(sendi);
                        return;
                    }
                }
            if (flag == null) {
                MessagesCore.INVALIDFLAG.send(sendi);
                return;
            }
            Claim claim = handler.getClaimMain(p.getLocation());
            if (claim != null) {
                if (claim.isOwner(p)) {
                    HelperClaim.setFlag(p, claim, flag, value);
                } else
                    MessagesCore.NOPERMISSION.send(sendi, claim);
            } else {
                MessagesCore.CANNOTEDIT.send(sendi);
            }
        } else
            Message.sms(p, "Usage: /claim flag FLAG_TYPE VALUE", null);
    }

    public boolean permission(CommandSender sendi) {
        return PermissionNodes.USE.check(sendi);
    }

    @Override
    public String getHelp() {
        return MessagesHelp.FLAGS.get();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            for (CLAIM_FLAG flag : CLAIM_FLAG.values())
                if (flag.name().startsWith(args[1].toUpperCase()))
                    list.add(flag.name().toLowerCase());
        } else if (args.length == 3)
            list.addAll(Arrays.asList("true", "false"));
        return list;
    }
}
