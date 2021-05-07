package me.RonanCraft.Pueblos.resources.tools;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.player.events.PlayerClaimInteraction;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_ERRORS;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_FLAG;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_MODE;
import me.RonanCraft.Pueblos.resources.claims.*;
import me.RonanCraft.Pueblos.resources.claims.ClaimMain;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_TYPE;
import me.RonanCraft.Pueblos.resources.database.DatabaseClaims;
import me.RonanCraft.Pueblos.resources.files.msgs.Message;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
import me.RonanCraft.Pueblos.resources.tools.visual.Visualization;
import me.RonanCraft.Pueblos.resources.tools.visual.VisualizationType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class HelperClaim {

    public static void toggleFlag(Player p, ClaimMain claim, CLAIM_FLAG flag) {
        setFlag(p, claim, flag, !(Boolean) claim.getFlags().getFlag(flag));
    }

    public static void setFlag(Player p, ClaimMain claim, CLAIM_FLAG flag, Object value) {
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
            Visualization.fromClaim(claim, p.getLocation().getBlockY(), VisualizationType.CLAIM, p.getLocation()).apply(p);
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

    public static ClaimMain createClaimMain(@Nonnull BoundingBox box, @Nullable UUID ownerID, @Nullable String ownerName, boolean admin_claim) {
        if (ownerID != null && !admin_claim) //Is this an admin claim?
            return new ClaimMain(ownerID, ownerName, box);
        else
            return new ClaimMain(box);
    }

    public static ClaimChild createClaimSub(BoundingBox box, ClaimMain parent) {
        return new ClaimChild(box, parent);
    }

    public static CLAIM_ERRORS registerClaim(@Nonnull Player creator, @Nonnull World world, @Nonnull Location pos1,
                                             @Nonnull Location pos2, boolean sendMsg,
                                             @Nullable PlayerClaimInteraction claimInteraction, CLAIM_TYPE type) {
        CLAIM_ERRORS error;
        ClaimHandler handler = Pueblos.getInstance().getClaimHandler();
        BoundingBox box = new BoundingBox(pos1, pos2);
        Claim claim;
        if (claimInteraction == null || type == CLAIM_TYPE.MAIN)
            claim = createClaimMain(box, creator.getUniqueId(), creator.getName(), claimInteraction != null && claimInteraction.mode == CLAIM_MODE.CREATE_ADMIN);
        else
            claim = createClaimSub(box, (ClaimMain) claimInteraction.editing);
        if (!HelperEvent.claimAttemptCreate(claim, creator).isCancelled()) {
            error = handler.uploadCreatedClaim(claim, creator, claimInteraction);
            switch (error) {
                case NONE:
                    MessagesCore.CLAIM_CREATE_SUCCESS.send(creator, claim);
                    Visualization.fromClaim(claim, creator.getLocation().getBlockY(), VisualizationType.CLAIM, creator.getLocation()).apply(creator);
                case SIZE_SMALL:
                case SIZE_LARGE:
                case OVERLAPPING:
                    break;
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

    public static void teleportTo(Player p, ClaimMain claim) {
        if (!HelperEvent.teleportToClaim(p, claim, p, p.getLocation()).isCancelled()) {
            //p.teleport(claim.getBoundingBox().getGreaterBoundaryCorner());
            MessagesCore.CLAIM_TELEPORT.send(p, claim);
        }
    }

    public static void deleteClaim(Player p, ClaimMain claim) {
        ClaimHandler handler = Pueblos.getInstance().getClaimHandler();
        CLAIM_ERRORS error = handler.deleteClaim(p, claim);
        if (error == CLAIM_ERRORS.NONE)
            MessagesCore.CLAIM_DELETE.send(p, claim);
        else
            error.sendMsg(p, claim);
    }

    public static void sendClaimInfo(Player p, ClaimMain claim) {
        List<String> msg = Pueblos.getInstance().getFiles().getLang().getStringList("ClaimInfo");
        Message.sms(p, msg, claim);
    }

    @Nullable
    public static Claim loadClaim(ResultSet result, CLAIM_TYPE toLoad) throws SQLException {
        UUID id = null;
        try {
            id = UUID.fromString(result.getString(DatabaseClaims.COLUMNS.OWNER_UUID.name));
        } catch (IllegalArgumentException e) {
            //id = UUID.randomUUID();
        }
        String name = result.getString(DatabaseClaims.COLUMNS.OWNER_NAME.name);
        try {
            Claim claim;
            BoundingBox position = JSONEncoding.getPosition(result.getString(DatabaseClaims.COLUMNS.POSITION.name));
            int _claim_id = result.getInt(DatabaseClaims.COLUMNS.CLAIM_ID.name);
            if (position == null) {
                Pueblos.getInstance().getLogger().severe("Claim " + _claim_id + " does not have a valid location! Claim was not registered!");
                return null;
            }
            if (toLoad == CLAIM_TYPE.MAIN) { //Loading main parent claims
                if (result.getLong(DatabaseClaims.COLUMNS.PARENT.name) == -1) {
                    if (result.getBoolean(DatabaseClaims.COLUMNS.ADMIN_CLAIM.name) || id == null)
                        claim = new ClaimMain(position);
                    else
                        claim = new ClaimMain(id, name, position);
                } else //Skip children claims
                    return null;
            } else {
                if (result.getLong(DatabaseClaims.COLUMNS.PARENT.name) > -1) {
                    ClaimMain parent = Pueblos.getInstance().getClaimHandler().getClaimMain((int) result.getLong(DatabaseClaims.COLUMNS.PARENT.name));
                    if (parent != null)
                        claim = new ClaimChild(position, parent);
                    else {
                        Pueblos.getInstance().getLogger().severe("A child claim exists without a parent! Please delete claim ID " + _claim_id);
                        return null;
                    }
                } else //Skip parent claims
                    return null;
            }
            //Members Load
            List<ClaimMember> members = JSONEncoding.getMember(result.getString(DatabaseClaims.COLUMNS.MEMBERS.name), claim);
            if (members != null)
                for (ClaimMember member : members)
                    claim.addMember(member, false);

            //Flags Load
            HashMap<CLAIM_FLAG, Object> flags = JSONEncoding.getFlags(result.getString(DatabaseClaims.COLUMNS.FLAGS.name));
            if (flags != null)
                for (Map.Entry<CLAIM_FLAG, Object> flag : flags.entrySet())
                    claim.getFlags().setFlag(flag.getKey(), flag.getValue(), false);

            //Join Requests Load
            List<ClaimRequest> requests = JSONEncoding.getRequests(result.getString(DatabaseClaims.COLUMNS.REQUESTS.name), claim);
            if (requests != null)
                for (ClaimRequest request : requests)
                    claim.addRequest(request, false);
            claim.claimId = result.getInt(DatabaseClaims.COLUMNS.CLAIM_ID.name);
            claim.dateCreated = HelperDate.getDate(result.getString(DatabaseClaims.COLUMNS.DATE.name));
            return claim;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
