/*
 * Copyright (c) 2022 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.BetterClaims.resources.helper;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.claims.data.BoundingBox;
import me.RonanCraft.BetterClaims.player.events.PlayerClaimInteraction;
import me.RonanCraft.BetterClaims.resources.Settings;
import me.RonanCraft.BetterClaims.claims.*;
import me.RonanCraft.BetterClaims.claims.ClaimHandler;
import me.RonanCraft.BetterClaims.claims.data.Claim_Request;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_ERRORS;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_FLAG;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_MODE;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_TYPE;
import me.RonanCraft.BetterClaims.claims.data.members.Member;
import me.RonanCraft.BetterClaims.database.DatabaseClaims;
import me.RonanCraft.BetterClaims.resources.messages.Message;
import me.RonanCraft.BetterClaims.resources.messages.MessagesCore;
import me.RonanCraft.BetterClaims.resources.visualization.Visualization;
import me.RonanCraft.BetterClaims.resources.visualization.VisualizationType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class HelperClaim {

    public static void toggleFlag(Player p, ClaimData claimData, CLAIM_FLAG flag) {
        setFlag(p, claimData, flag, !(Boolean) claimData.getFlags().getFlag(flag));
    }

    public static void setFlag(Player p, ClaimData claimData, CLAIM_FLAG flag, Object value) {
        claimData.getFlags().setFlag(flag, value, true);
        MessagesCore.CLAIM_FLAGCHANGE.send(p, new Object[]{claimData, flag});
    }

    public static boolean requestJoin(Player p, ClaimData claimData) {
        if (claimData.hasRequestFrom(p)) { //Already has a request
            MessagesCore.REQUEST_REQUESTER_ALREADY.send(p, claimData.getRequest(p));
            return false;
        } else { //New Request
            Claim_Request request = new Claim_Request(p.getUniqueId(), p.getName(), Calendar.getInstance().getTime(), claimData);
            claimData.addRequest(request, true);
            MessagesCore.REQUEST_REQUESTER_SENT.send(p, request);
            Visualization.fromClaim(claimData, p.getLocation().getBlockY(), VisualizationType.CLAIM, p.getLocation()).apply(p);
            if (Objects.requireNonNull(claimData.getOwner()).isOnline())
                MessagesCore.REQUEST_NEW.send((Player) claimData.getOwner(), request);
        }
        return true;
    }

    public static void requestAction(boolean accepted, Player p, Claim_Request request) {
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

    public static void leaveClaim(Player p, Member member) {
        if (!HelperEvent.memberLeave(p, member).isCancelled()) {
            member.claimData.removeMember(member, true);
            MessagesCore.CLAIM_MEMBER_LEAVE.send(p, member);
            if (Objects.requireNonNull(member.claimData.getOwner()).isOnline())
                MessagesCore.CLAIM_MEMBER_NOTIFICATION_LEAVE.send(member.claimData.getOwner().getPlayer(), member);
        }
    }

    public static void removeMember(Player p, Member member) {
        if (!HelperEvent.memberLeave(p, member).isCancelled()) {
            member.claimData.removeMember(member, true);
            MessagesCore.CLAIM_MEMBER_REMOVED.send(p, member);
            if (member.getPlayer().isOnline())
                MessagesCore.CLAIM_MEMBER_NOTIFICATION_REMOVED.send(member.getPlayer().getPlayer(), member);
        }
    }

    public static Claim createClaimMain(@NotNull BoundingBox box, @Nullable UUID ownerID, @Nullable String ownerName, boolean admin_claim) {
        if (ownerID != null && !admin_claim) //Is this an admin claim?
            return new Claim(ownerID, ownerName, box);
        else
            return new Claim(box);
    }

    public static Claim_Child createClaimSub(BoundingBox box, Claim parent) {
        return new Claim_Child(box, parent);
    }

    public static CLAIM_ERRORS registerClaim(@NotNull Player creator, @NotNull World world, @NotNull Location pos1,
                                             @NotNull Location pos2, @Nullable PlayerClaimInteraction claimInteraction, CLAIM_TYPE type) {
        CLAIM_ERRORS error;
        ClaimHandler handler = BetterClaims.getInstance().getClaimHandler();
        BoundingBox box = new BoundingBox(pos1, pos2);
        ClaimData claimData;

        //World Enabled
        if (!worldEnabled(world)) return CLAIM_ERRORS.WORLD_DISABLED;

        //Create claim
        if (claimInteraction == null || type == CLAIM_TYPE.PARENT)
            claimData = createClaimMain(box, creator.getUniqueId(), creator.getName(), claimInteraction != null && claimInteraction.mode == CLAIM_MODE.CREATE_ADMIN);
        else
            claimData = createClaimSub(box, (Claim) claimInteraction.editing);

        //Claim Max Count - on normal claim
        if (claimData.claimType == CLAIM_TYPE.PARENT && !claimData.isAdminClaim() &&
                BetterClaims.getInstance().getClaimHandler().getClaims(creator.getUniqueId()).size() >= BetterClaims.getInstance().getSettings().getInt(Settings.SETTING.CLAIM_COUNT))
            return CLAIM_ERRORS.CLAIM_COUNT;

        //Save claim/send error if any
        if (!HelperEvent.claimAttemptCreate(claimData, creator).isCancelled()) {
            error = handler.uploadCreatedClaim(claimData, creator, claimInteraction);
            switch (error) {
                case NONE:
                    MessagesCore.CLAIM_CREATE_SUCCESS.send(creator, claimData);
                    Visualization.fromClaim(claimData, creator.getLocation().getBlockY(), VisualizationType.CLAIM, creator.getLocation()).apply(creator);
                case SIZE_SMALL:
                case SIZE_LARGE:
                case OVERLAPPING:
                    break;
            }
            //if (sendMsg)
            //    error.sendMsg(creator, claim);
        } else
            error = CLAIM_ERRORS.CANCELLED;
        return error;
    }

    public static String getLocationString(ClaimData claimData) {
        BoundingBox pos = claimData.getBoundingBox();
        return "x: " + pos.getLeft() + " z: " + pos.getTop();
    }

    public static void teleportTo(Player p, ClaimData claimData) {
        if (!HelperEvent.teleportToClaim(p, claimData, p, p.getLocation()).isCancelled()) {
            Location loc = claimData.getGreaterBoundaryCorner();
            loc.setY(Objects.requireNonNull(loc.getWorld()).getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()) + 1);
            p.teleport(loc);
            MessagesCore.CLAIM_TELEPORT.send(p, claimData);
        }
    }

    public static void deleteClaim(Player p, Claim claim) {
        Bukkit.getScheduler().runTaskAsynchronously(BetterClaims.getInstance(), () -> {
            ClaimHandler handler = BetterClaims.getInstance().getClaimHandler();
            CLAIM_ERRORS error = handler.deleteClaim(p, claim);
            if (error == CLAIM_ERRORS.NONE)
                MessagesCore.CLAIM_DELETE.send(p, claim);
            else
                error.sendMsg(p, claim);
        });
    }

    public static void sendClaimInfo(Player p, ClaimData claimData) {
        List<String> msg = BetterClaims.getInstance().getFiles().getLang().getStringList("ClaimInfo");
        Message.sms(p, msg, claimData);
    }

    @Nullable
    public static ClaimData loadClaim(ResultSet result, CLAIM_TYPE toLoad, List<ClaimData> parent_claims) throws SQLException {
        UUID id = null;
        try {
            id = UUID.fromString(result.getString(DatabaseClaims.COLUMNS.OWNER_UUID.name));
        } catch (IllegalArgumentException e) {
            //id = UUID.randomUUID();
        }
        String name = result.getString(DatabaseClaims.COLUMNS.OWNER_NAME.name);
        try {
            ClaimData claimData;
            BoundingBox position = HelperJSON.getPosition(result.getString(DatabaseClaims.COLUMNS.POSITION.name));
            int _claim_id = result.getInt(DatabaseClaims.COLUMNS.CLAIM_ID.name);
            if (position == null) {
                BetterClaims.getInstance().getLogger().severe("Claim " + _claim_id + " does not have a valid location! Claim was not registered!");
                return null;
            }
            if (toLoad == CLAIM_TYPE.PARENT) { //Loading main parent claims
                if (result.getLong(DatabaseClaims.COLUMNS.PARENT.name) == -1) {
                    if (result.getBoolean(DatabaseClaims.COLUMNS.ADMIN_CLAIM.name) || id == null)
                        claimData = new Claim(position);
                    else
                        claimData = new Claim(id, name, position);
                } else //Skip children claims
                    return null;
            } else {
                if (result.getLong(DatabaseClaims.COLUMNS.PARENT.name) > -1) {
                    int _parent_id = result.getInt(DatabaseClaims.COLUMNS.PARENT.name);
                    Claim parent = null;
                    for (ClaimData _pclaim : parent_claims)
                        if (_pclaim.claimId == _parent_id) {
                            parent = (Claim) _pclaim;
                            break;
                        }
                    if (parent != null)
                        claimData = new Claim_Child(position, parent);
                    else {
                        BetterClaims.getInstance().getLogger().severe("A child claim exists without the parent Claim #" + _parent_id + "!" +
                                " Please delete claim ID " + _claim_id);
                        return null;
                    }
                } else //Skip parent claims
                    return null;
            }
            //Members Load
            List<Member> members = HelperJSON.getMember(result.getString(DatabaseClaims.COLUMNS.MEMBERS.name), claimData);
            if (members != null)
                for (Member member : members)
                    claimData.addMember(member, false);

            //Flags Load
            HashMap<CLAIM_FLAG, Object> flags = HelperJSON.getFlags(result.getString(DatabaseClaims.COLUMNS.FLAGS.name));
            if (flags != null)
                for (Map.Entry<CLAIM_FLAG, Object> flag : flags.entrySet())
                    claimData.getFlags().setFlag(flag.getKey(), flag.getValue(), false);

            //Join Requests Load
            List<Claim_Request> requests = HelperJSON.getRequests(result.getString(DatabaseClaims.COLUMNS.REQUESTS.name), claimData);
            if (requests != null)
                for (Claim_Request request : requests)
                    claimData.addRequest(request, false);
            claimData.claimId = result.getInt(DatabaseClaims.COLUMNS.CLAIM_ID.name);
            claimData.dateCreated = HelperDate.getDate(result.getString(DatabaseClaims.COLUMNS.DATE.name));
            return claimData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void rename(ClaimData c, String newName) {
        c.setClaimName(newName, true);
    }

    public static boolean worldEnabled(World world) {
        return BetterClaims.getInstance().getSettings().getStringList(Settings.SETTING.CLAIM_WORLDS_ENABLED).contains(world.getName());
    }
}
