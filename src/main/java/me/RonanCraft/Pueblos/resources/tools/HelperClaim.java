package me.RonanCraft.Pueblos.resources.tools;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.player.events.PlayerClaimInteraction;
import me.RonanCraft.Pueblos.resources.claims.*;
import me.RonanCraft.Pueblos.resources.files.msgs.Message;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
import me.RonanCraft.Pueblos.resources.tools.visual.Visualization;
import me.RonanCraft.Pueblos.resources.tools.visual.VisualizationType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Calendar;

public class HelperClaim {

    public static void toggleFlag(Player p, Claim claim, CLAIM_FLAG flag) {
        setFlag(p, claim, flag, !(Boolean) claim.getFlags().getFlag(flag));
    }

    public static void setFlag(Player p, Claim claim, CLAIM_FLAG flag, Object value) {
        claim.getFlags().setFlag(flag, value, true);
        MessagesCore.CLAIM_FLAGCHANGE.send(p, new Object[]{claim, flag});
    }

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
        if (!HelperEvent.memberLeave(member).isCancelled()) {
            member.claim.removeMember(member, true);
            MessagesCore.CLAIM_MEMBER_LEAVE.send(p, member);
            if (member.claim.getOwner().isOnline())
                MessagesCore.CLAIM_MEMBER_NOTIFICATION_LEAVE.send(member.claim.getOwner().getPlayer(), member);
        }
    }

    public static void removeMember(Player p, ClaimMember member) {
        if (!HelperEvent.memberLeave(member).isCancelled()) {
            member.claim.removeMember(member, true);
            MessagesCore.CLAIM_MEMBER_REMOVED.send(p, member);
            if (member.getPlayer().isOnline())
                MessagesCore.CLAIM_MEMBER_NOTIFICATION_REMOVED.send(member.getPlayer().getPlayer(), member);
        }
    }

    public static CLAIM_ERRORS createClaim(Player owner, Location pos1, Location pos2, boolean sendMsg, PlayerClaimInteraction.CLAIM_MODE mode) {
        CLAIM_ERRORS error;
        ClaimHandler handler = Pueblos.getInstance().getSystems().getClaimHandler();
        Claim claim = handler.claimCreate(owner.getUniqueId(), owner.getName(), new ClaimPosition(owner.getWorld(), pos1, pos2), mode);
        if (!HelperEvent.claimAttemptCreate(claim, owner).isCancelled()) {
            if (claim != null) {
                error = handler.uploadClaim(claim, owner);
                switch (error) {
                    case NONE:
                        MessagesCore.CLAIM_CREATE_SUCCESS.send(owner, claim);
                        Visualization.fromClaim(claim, owner.getLocation().getBlockY(), VisualizationType.CLAIM, owner.getLocation()).apply(owner);
                    case SIZE_SMALL:
                    case SIZE_LARGE:
                    case OVERLAPPING:
                        break;
                    default:
                        Message.sms(owner, "An Error Happened!", null);
                }
            } else { //Overlapping
                //MessagesCore.CLAIM_CREATE_FAILED_OTHERCLAIM.send(owner);
                error = CLAIM_ERRORS.OVERLAPPING;
            }
            if (sendMsg)
                error.sendMsg(owner, claim);
        } else
            error = CLAIM_ERRORS.CANCELLED;
        return error;
    }

    public static String getLocationString(Claim claim) {
        ClaimPosition pos = claim.getPosition();
        return pos.getLeft() + "x, " + pos.getTop() + "z";
    }

    public static void teleportTo(Player p, Claim claim) {
        if (!HelperEvent.teleportToClaim(claim, p, p.getLocation()).isCancelled()) {
            p.teleport(claim.getPosition().getLocation());
            MessagesCore.CLAIM_TELEPORT.send(p, claim);
        }
    }
}
