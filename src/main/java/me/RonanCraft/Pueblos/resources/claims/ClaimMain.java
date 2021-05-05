/*
 * Copyright (c) 2021 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_PERMISSION_LEVEL;
import me.RonanCraft.Pueblos.resources.tools.HelperEvent;
import me.RonanCraft.Pueblos.resources.tools.JSONEncoding;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ClaimMain extends Claim {

    public ClaimMain parent;
    //public long claimId; //ID given by the database
    //Claim Information
    private boolean adminClaim;

    private String name;
    public Date dateCreated;
    public boolean deleted = false;

    ClaimMain(BoundingBox boundingBox) {
        this(null, null, boundingBox);
    }

    //Get


    //Is
    public boolean isOwner(Player p) {
        return p.getUniqueId().equals(ownerId);
    }

    public boolean isMember(Player p) {
        return members.isMember(p.getUniqueId());
    }

    //Add
    public void addMember(ClaimMember member, boolean update) {
        if (member != null) {
            for (ClaimMember _member : members.getMembers()) //Duplicate Member, lets remove them from the database too
                if (_member.uuid.equals(member.uuid)) {
                    updated();
                    return;
                }
            members.addMember(member, update);
        }
    }

    public void addRequest(ClaimRequest request, boolean update) {
        if (update) updated();
        this.requests.add(request);
    }

    //Get
    @Override
    public OfflinePlayer getOwner() {
        return ownerId == null ? null : Bukkit.getOfflinePlayer(ownerId);
    }

    @Override
    public ClaimFlags getFlags() {
        return flags;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    @Override
    public List<ClaimMember> getMembers() {
        return members.getMembers();
    }

    public ClaimMember getMember(Player p) {
        return members.getMember(p.getUniqueId());
    }

    public boolean canBuild(Player p) {
        return isOwner(p) || isMember(p);
    }


    @Override
    public void removeMember(ClaimMember member, boolean update) {
        members.remove(member, update);
    }

    //Checks
    public boolean contains(Location loc) {
        return boundingBox.contains(loc);
        /*(loc.getBlockY() >= Pueblos.getInstance().getSettings().getInt(Settings.SETTING.CLAIM_MAXDEPTH)
                && (boundingBox.getLeft() <= loc.getBlockX() && boundingBox.getTop() >= loc.getBlockZ()) && //Top Left
                (boundingBox.getRight() >= loc.getBlockX() && boundingBox.getBottom() <= loc.getBlockZ())); //Bottom Right*/
    }

    public boolean hasRequestFrom(Player p) {
        return getRequest(p) != null;
    }

    public ClaimRequest getRequest(Player p) {
        for (ClaimRequest request : requests)
            if (request.id.equals(p.getUniqueId()))
                return request;
        return null;
    }

    @Override
    public boolean checkPermLevel(@Nonnull Player p, CLAIM_PERMISSION_LEVEL level) {
        if (level != null)
            switch (level) {
                case OWNER: return isOwner(p) || (isAdminClaim() && PermissionNodes.ADMIN_CLAIM.check(p));
                case MEMBER: return isMember(p);
                default: return true;
            }
        return true;
    }

    public boolean editCorners(@Nonnull Player editor, Location loc_1, Location loc_2) {
        if (!HelperEvent.claimResize(editor, this, editor, loc_1, loc_2).isCancelled()) {
            getBoundingBox().editCorners(loc_1, loc_2);
            updated();
            return true;
        } else
            return false;
    }



    void changeOwner(@Nullable UUID newOwnerId, boolean save_oldOwner_as_Member) {
        UUID oldOwnerId = this.ownerId;
        this.ownerId = newOwnerId;
        this.ownerName = getOwner() == null ? "Admin Claim" : getOwner().getName();
        this.adminClaim = this.ownerId == null;
        if (save_oldOwner_as_Member && oldOwnerId != null) { //Make old owner a member
            OfflinePlayer oldOwner = Bukkit.getPlayer(oldOwnerId);
            if (oldOwner != null) {
                members.addMember(new ClaimMember(oldOwnerId, oldOwner.getName(), Calendar.getInstance().getTime(), this), true);
            }
        }
        if (this.ownerId != null && oldOwnerId != null) { //Remove the new owner as a member, if they were a member
            if (members.isMember(oldOwnerId))
                members.remove(members.getMember(oldOwnerId), true);
        }
        updated();
    }
}
