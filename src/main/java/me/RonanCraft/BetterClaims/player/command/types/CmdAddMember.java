package me.RonanCraft.BetterClaims.player.command.types;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.player.command.ClaimCommand;
import me.RonanCraft.BetterClaims.player.command.ClaimCommandHelpable;
import me.RonanCraft.BetterClaims.player.command.ClaimCommandTabComplete;
import me.RonanCraft.BetterClaims.resources.PermissionNodes;
import me.RonanCraft.BetterClaims.claims.ClaimHandler;
import me.RonanCraft.BetterClaims.claims.Claim;
import me.RonanCraft.BetterClaims.claims.data.members.Member;
import me.RonanCraft.BetterClaims.resources.helper.HelperClaim;
import me.RonanCraft.BetterClaims.resources.messages.MessagesCore;
import me.RonanCraft.BetterClaims.resources.messages.MessagesHelp;
import me.RonanCraft.BetterClaims.resources.messages.MessagesUsage;
import me.RonanCraft.BetterClaims.resources.helper.HelperDate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdAddMember implements ClaimCommand, ClaimCommandHelpable, ClaimCommandTabComplete {

    public String getName() {
        return "addMember";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;
        Claim claim = HelperClaim.getHandler().getClaimMain(p.getLocation());
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
