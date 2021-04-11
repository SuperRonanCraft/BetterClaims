package me.RonanCraft.Pueblos.resources.tools;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import me.RonanCraft.Pueblos.resources.claims.ClaimRequest;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
import org.bukkit.entity.Player;

import java.util.Calendar;

public class HelperClaim {

    public static boolean requestJoin(Player p, Claim claim) {
        if (claim.hasRequestFrom(p)) { //Already has a request
            MessagesCore.REQUEST_REQUESTER_ALREADY.send(p, claim.getRequest(p));
            return false;
        } else { //New Request
            ClaimRequest request = new ClaimRequest(p.getUniqueId(), p.getName(), Calendar.getInstance().getTime(), claim);
            claim.addRequest(request, true);
            MessagesCore.REQUEST_REQUESTER_SENT.send(p, request);
            if (claim.getOwner().isOnline())
                MessagesCore.REQUEST_NEW.send((Player) claim.getOwner(), request);
        }
        return true;
    }

    public static void requestAction(boolean accepted, Player p, ClaimRequest request) {
        if (accepted) {
            request.accepted();
            MessagesCore.REQUEST_ACCEPTED.send(p, request);
            if (request.getPlayer().isOnline())
                MessagesCore.REQUEST_REQUESTER_ACCEPTED.send(request.getPlayer().getPlayer(), request);
        } else {
            request.declined();
            MessagesCore.REQUEST_DENIED.send(p, request);
            if (request.getPlayer().isOnline())
                MessagesCore.REQUEST_REQUESTER_DENIED.send(request.getPlayer().getPlayer(), request);
        }
        /*if (claim.hasRequestFrom(p)) { //Already has a request
            MessagesCore.REQUEST_ALREADY.send(p);
            return false;
        } else { //New Request
            ClaimRequest request = new ClaimRequest(p.getUniqueId(), p.getName(), Calendar.getInstance().getTime(), claim);
            claim.addRequest(request, true);
            MessagesCore.REQUEST_SENT.send(p);
            if (claim.getOwner().isOnline())
                MessagesCore.REQUEST_NEW.send((Player) claim.getOwner());
        }*/
    }

    public static void leaveClaim(Player p, ClaimMember member) {
        member.claim.removeMember(member, true);
        MessagesCore.CLAIM_MEMBER_LEAVE.send(p, member);
        if (member.claim.getOwner().isOnline())
            MessagesCore.CLAIM_MEMBER_NOTIFICATION_LEAVE.send(member.claim.getOwner().getPlayer(), member);
    }

    public static void removeMember(Player p, ClaimMember member) {
        member.claim.removeMember(member, true);
        MessagesCore.CLAIM_MEMBER_REMOVED.send(p, member);
        if (member.getPlayer().isOnline())
            MessagesCore.CLAIM_MEMBER_NOTIFICATION_REMOVED.send(member.getPlayer().getPlayer(), member);
    }
}
