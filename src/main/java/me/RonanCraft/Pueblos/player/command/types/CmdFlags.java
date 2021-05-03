package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.player.command.PueblosCommandTabComplete;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.claims.*;
import me.RonanCraft.Pueblos.resources.files.msgs.Message;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesHelp;
import me.RonanCraft.Pueblos.resources.tools.HelperClaim;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdFlags implements PueblosCommand, PueblosCommandHelpable, PueblosCommandTabComplete {

    public String getName() {
        return "flags";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;
        ClaimHandler handler = Pueblos.getInstance().getClaimHandler();

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
            Claim claim = handler.getClaim(p.getLocation());
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
