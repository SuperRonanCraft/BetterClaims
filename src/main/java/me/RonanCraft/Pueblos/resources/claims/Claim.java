package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.tools.HelperEvent;
import me.RonanCraft.Pueblos.resources.tools.JSONEncoding;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class Claim {
    private UUID ownerId;
    private String ownerName;
    private Claim parent;
    public long claimId; //ID given by the database
    //Claim Information
    private boolean adminClaim;
    private final BoundingBox boundingBox;
    private final ClaimFlags flags = new ClaimFlags(this);
    private final ClaimMembers members = new ClaimMembers(this);
    private final List<ClaimRequest> requests = new ArrayList<>();
    private String name;
    public Date dateCreated;
    public boolean deleted = false;
    //Database stuff
    private boolean updated = false;

    Claim(UUID ownerId, String ownerName, BoundingBox boundingBox) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.boundingBox = boundingBox;
        this.adminClaim = this.ownerId == null;
    }

    Claim(BoundingBox boundingBox) {
        this(null, null, boundingBox);
    }

    //Get
    public UUID getOwnerID() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getClaimName() {
        return name != null ? name : (ownerName != null ? ownerName : (!isAdminClaim() ? getOwner().getName() : "Admin Claim"));
    }

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
    @Nullable
    public OfflinePlayer getOwner() {
        return ownerId == null ? null : Bukkit.getOfflinePlayer(ownerId);
    }

    public ClaimFlags getFlags() {
        return flags;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public String getBoundingBoxJSON() {
        return JSONEncoding.getJsonFromBoundingBox(boundingBox);
    }

    public List<ClaimMember> getMembers() {
        return members.getMembers();
    }

    public ClaimMember getMember(Player p) {
        return members.getMember(p.getUniqueId());
    }

    public boolean canBuild(Player p) {
        return isOwner(p) || isMember(p);
    }

    public List<ClaimRequest> getRequests() {
        return requests;
    }

    public void removeRequest(ClaimRequest request, boolean update) {
        if (update)
            updated();
        requests.remove(request);
    }

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

    //Database
    public void updated() {
        updated = true;
    }

    public boolean wasUpdated() {
        return updated;
    }

    public void uploaded() {
        updated = false;
    }

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

    public boolean isAdminClaim() {
        return adminClaim;
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

    @Nullable
    public Claim getParent() {
        return this.parent;
    }
}
