package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.player.command.PueblosCommandTabComplete;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.claims.ClaimHandler;
import me.RonanCraft.Pueblos.claims.Claim;
import me.RonanCraft.Pueblos.claims.data.members.Member;
import me.RonanCraft.Pueblos.resources.messages.MessagesCore;
import me.RonanCraft.Pueblos.resources.messages.MessagesHelp;
import me.RonanCraft.Pueblos.resources.messages.MessagesUsage;
import me.RonanCraft.Pueblos.resources.helper.HelperDate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdAddMember implements PueblosCommand, PueblosCommandHelpable, PueblosCommandTabComplete {

    public String getName() {
        return "addMember";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        ClaimHandler handler = Pueblos.getInstance().getClaimHandler();
        Player p = (Player) sendi;
        Claim claim = handler.getClaimMain(p.getLocation());
        if (args.length >= 2) {
            Player playerAdd = Bukkit.getPlayer(args[1]);
            if (playerAdd != null) {
                if (claim != null) {
                    if (claim.isOwner(p)) {
                        if (!playerAdd.equals(p)) {
                            if (!claim.isMember(playerAdd)) {
                                Member member = new Member(playerAdd.getUniqueId(), playerAdd.getName(), HelperDate.getDate(), claim);
                                claim.addMember(member, true);
                                MessagesCore.CLAIM_MEMBER_ADDED.send(sendi, member);
                            } else
                                MessagesCore.CLAIM_MEMBER_EXISTS.send(sendi);
                        } else
                            MessagesCore.CLAIM_MEMBER_SELF.send(sendi);
                    } else
                        MessagesCore.CLAIM_PERMISSION_CLAIM.send(sendi);
                } else
                    MessagesCore.CLAIM_NOTINSIDE.send(sendi);
            } else {
                MessagesCore.PLAYER_EXIST.send(sendi, args[1]);
            }
        } else
            usage(sendi, label);
    }

    public boolean permission(CommandSender sendi) {
        return PermissionNodes.USE.check(sendi);
    }

    @Override
    public String getHelp() {
        return MessagesHelp.LIST.get();
    }

    private void usage(CommandSender sendi, String label) {
        MessagesUsage.CLAIM_ADDMEMBER.send(sendi, label);
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2)
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[1].toLowerCase()) && !p.getName().equals(sendi.getName()))
                    list.add(p.getName());
            }
        return list;
    }
}
