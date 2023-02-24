/*
 * Copyright (c) 2021 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.BetterClaims.claims;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.claims.data.BoundingBox;
import me.RonanCraft.BetterClaims.resources.PermissionNodes;
import me.RonanCraft.BetterClaims.claims.data.Claim_Request;
import me.RonanCraft.BetterClaims.claims.data.Claim_FlagHandler;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_ERRORS;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_PERMISSION_LEVEL;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_TYPE;
import me.RonanCraft.BetterClaims.claims.data.members.ClaimMembers;
import me.RonanCraft.BetterClaims.claims.data.members.Member;
import me.RonanCraft.BetterClaims.auction.Auction;
import me.RonanCraft.BetterClaims.resources.helper.HelperEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class ClaimData {

    public long claimId;
    UUID ownerId;
    String ownerName;
    private final BoundingBox boundingBox;
    public boolean deleted;
    //Loaded after
    private String claimName;
    private final Claim_FlagHandler flags = new Claim_FlagHandler(this);
    final ClaimMembers members = new ClaimMembers(this);
    private final List<Claim_Request> requests = new ArrayList<>();
    public Date dateCreated;
    //Claim info
    boolean adminClaim;
    public final CLAIM_TYPE claimType;
    boolean updated = false;

    ClaimData(BoundingBox boundingBox) {
        this(null, null, boundingBox);
    }

    ClaimData(UUID ownerId, String ownerName, @NotNull BoundingBox boundingBox) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.boundingBox = boundingBox;
        this.adminClaim = this.ownerId == null;
        claimType = this instanceof Claim ? CLAIM_TYPE.PARENT : CLAIM_TYPE.CHILD;
    }

    public boolean contains(Location loc) {
        if (!Objects.equals(loc.getWorld(), getWorld()))
            return false;
        return boundingBox.contains(loc);
    }

    public Member getMember(Player p) {
        return members.getMember(p.getUniqueId());
    }

    public boolean canBuild(Player p) {
        return isOwner(p) || isMember(p);
    }

    public String getClaimName() {
        return claimName != null ? claimName : (ownerName != null ? ownerName : (!isAdminClaim() ? getOwner().getName() : "Admin Claim"));
    }

    public Claim_FlagHandler getFlags() {
        return flags;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public boolean isAdminClaim() {
        return adminClaim;
    }

    //Owner
    public boolean isOwner(Player p) {
        return p.getUniqueId().equals(ownerId);
    }

    public UUID getOwnerID() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Nullable
    public OfflinePlayer getOwner() {
        return ownerId == null ? null : Bukkit.getOfflinePlayer(ownerId);
    }
    //Members
    public List<Member> getMembers() {
        return members.getMembers();
    }

    public void removeMember(Member member, boolean update) {
        members.remove(member, update);
    }

    public void addMember(Member member, boolean update) {
        if (member != null) {
            for (Member _member : members.getMembers()) //Duplicate Member, lets remove them from the database too
                if (_member.uuid.equals(member.uuid)) {
                    updated(true);
                    return;
                }
            members.addMember(member, update);
        }
    }

    public boolean isMember(Player p) {
        return members.isMember(p.getUniqueId());
    }

    //Requests
    public List<Claim_Request> getRequests() {
        return requests;
    }

    public Claim_Request getRequest(Player p) {
        for (Claim_Request request : requests)
            if (request.id.equals(p.getUniqueId()))
                return request;
        return null;
    }

    public void addRequest(Claim_Request request, boolean update) {
        updated(update);
        this.requests.add(request);
    }

    public void removeRequest(Claim_Request request, boolean update) {
        updated(update);
        requests.remove(request);
    }

    public boolean hasRequestFrom(Player p) {
        return getRequest(p) != null;
    }

    //Tools
    public boolean checkPermLevel(@NotNull Player p, CLAIM_PERMISSION_LEVEL level) {
        if (level != null)
            switch (level) {
                case OWNER: return isOwner(p) || (isAdminClaim() && PermissionNodes.ADMIN_CLAIM.check(p));
                case MEMBER: return isMember(p) || checkPermLevel(p, CLAIM_PERMISSION_LEVEL.OWNER);
                default: return true;
            }
        return true;
    }

    public CLAIM_ERRORS editCorners(@NotNull Player editor, Vector loc_1, Vector loc_2) {
        if (!HelperEvent.claimResize(editor, this, editor, loc_1, loc_2).isCancelled()) {
            if (BetterClaims.getInstance().getClaimHandler().canResize(editor, this, new BoundingBox(editor.getWorld(), loc_1, loc_2))) {
                getBoundingBox().editCorners(loc_1, loc_2);
                updated(true);
                return CLAIM_ERRORS.NONE;
            } else {
                if (this instanceof Claim)
                    return CLAIM_ERRORS.RESIZE_OVERLAPPING_CHILD;
                return CLAIM_ERRORS.RESIZE_OVERLAPPING_PARENT;
            }
        } else
            return CLAIM_ERRORS.CANCELLED;
    }

    public Location getLesserBoundaryCorner() {
        return new Location(boundingBox.getWorld(), boundingBox.getLeft(), 0, boundingBox.getBottom());
    }

    public Location getGreaterBoundaryCorner() {
        return new Location(boundingBox.getWorld(), boundingBox.getRight(), 0, boundingBox.getTop());
    }

    @NotNull
    public World getWorld() {
        return boundingBox.getWorld();
    }

    public boolean isChild() {
        return this instanceof Claim_Child;
    }

    public Auction getAuction() {
        return BetterClaims.getInstance().getClaimHandler().getAuctionManager().getAuction(this);
    }

    public void setClaimName(String name, boolean update) {
        updated(update);
        this.claimName = name;
    }

    public void updated(boolean update) {
        if (update)
            updated = true;
    }

    public boolean wasUpdated() {
        return updated;
    }

    public void uploaded() {
        updated = false;
    }
}
