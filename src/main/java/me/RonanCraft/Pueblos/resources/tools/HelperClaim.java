package me.RonanCraft.Pueblos.resources.tools;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.CLAIM_MODE;
import me.RonanCraft.Pueblos.resources.claims.*;
import me.RonanCraft.Pueblos.resources.files.FileLanguage;
import me.RonanCraft.Pueblos.resources.files.msgs.Message;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
import me.RonanCraft.Pueblos.resources.tools.visual.Visualization;
import me.RonanCraft.Pueblos.resources.tools.visual.VisualizationType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

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
            if (Objects.requireNonNull(claim.getOwner()).isOnline())
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
        if (!HelperEvent.memberLeave(p, member).isCancelled()) {
            member.claim.removeMember(member, true);
            MessagesCore.CLAIM_MEMBER_LEAVE.send(p, member);
            if (Objects.requireNonNull(member.claim.getOwner()).isOnline())
                MessagesCore.CLAIM_MEMBER_NOTIFICATION_LEAVE.send(member.claim.getOwner().getPlayer(), member);
        }
    }

    public static void removeMember(Player p, ClaimMember member) {
        if (!HelperEvent.memberLeave(p, member).isCancelled()) {
            member.claim.removeMember(member, true);
            MessagesCore.CLAIM_MEMBER_REMOVED.send(p, member);
            if (member.getPlayer().isOnline())
                MessagesCore.CLAIM_MEMBER_NOTIFICATION_REMOVED.send(member.getPlayer().getPlayer(), member);
        }
    }

    public static CLAIM_ERRORS createClaim(@Nonnull Player creator, @Nonnull World world, @Nonnull Location pos1, @Nonnull Location pos2, boolean sendMsg, @Nonnull CLAIM_MODE mode) {
        CLAIM_ERRORS error;
        ClaimHandler handler = Pueblos.getInstance().getClaimHandler();
        Claim claim = handler.claimCreate(creator.getUniqueId(), creator.getName(), new BoundingBox(world, pos1, pos2), mode);
        if (!HelperEvent.claimAttemptCreate(claim, creator).isCancelled()) {
            if (claim != null) {
                error = handler.uploadClaim(claim, creator);
                switch (error) {
                    case NONE:
                        MessagesCore.CLAIM_CREATE_SUCCESS.send(creator, claim);
                        Visualization.fromClaim(claim, creator.getLocation().getBlockY(), VisualizationType.CLAIM, creator.getLocation()).apply(creator);
                    case SIZE_SMALL:
                    case SIZE_LARGE:
                    case OVERLAPPING:
                        break;
                    default:
                        Message.sms(creator, "An Error Happened!", null);
                }
            } else { //Overlapping
                //MessagesCore.CLAIM_CREATE_FAILED_OTHERCLAIM.send(owner);
                error = CLAIM_ERRORS.OVERLAPPING;
            }
            if (sendMsg)
                error.sendMsg(creator, claim);
        } else
            error = CLAIM_ERRORS.CANCELLED;
        return error;
    }

    public static String getLocationString(Claim claim) {
        BoundingBox pos = claim.getBoundingBox();
        return pos.getLeft() + "x, " + pos.getTop() + "z";
    }

    public static void teleportTo(Player p, Claim claim) {
        if (!HelperEvent.teleportToClaim(p, claim, p, p.getLocation()).isCancelled()) {
            p.teleport(claim.getBoundingBox().getLocation());
            MessagesCore.CLAIM_TELEPORT.send(p, claim);
        }
    }

    public static void deleteClaim(Player p, Claim claim) {
        ClaimHandler handler = Pueblos.getInstance().getClaimHandler();
        CLAIM_ERRORS error = handler.deleteClaim(p, claim);
        if (error == CLAIM_ERRORS.NONE)
            MessagesCore.CLAIM_DELETE.send(p, claim);
        else
            error.sendMsg(p, claim);
    }

    public static void sendClaimInfo(Player p, Claim claim) {
        List<String> msg = Pueblos.getInstance().getFiles().getLang().getStringList("ClaimInfo");
        Message.sms(p, msg, claim);
    }
}
