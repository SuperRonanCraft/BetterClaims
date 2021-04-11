package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.inventory.PueblosInventory;
import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.claims.*;
import me.RonanCraft.Pueblos.resources.files.msgs.Message;
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
                    }
                    //----
                    Pueblos.getInstance().getSystems().getDatabase().updateMembers(claim);
                    PueblosInventory.CLAIM.open(p, claim, true);
                } else
                    MessagesCore.CLAIM_NOPERMISSION.send(sendi, claim);
            } else
                MessagesCore.CLAIM_NOTINSIDE.send(sendi);
        }
    }

    public boolean permission(CommandSender sendi) {
        return true;
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
