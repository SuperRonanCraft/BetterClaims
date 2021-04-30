package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.Settings;
import me.RonanCraft.Pueblos.resources.tools.HelperEvent;
import me.RonanCraft.Pueblos.resources.tools.JSONEncoding;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

public class Claim implements ClaimInfo {
    private UUID ownerId;
    private String ownerName;
    public long claimId; //ID given by the database
    //Claim Information
    private boolean adminClaim;
    private final ClaimPosition position;
    private final ClaimFlags flags = new ClaimFlags(this);
    private final ClaimMembers members = new ClaimMembers(this);
    private final List<ClaimRequest> requests = new ArrayList<>();
    private String name;
    public Date dateCreated;
    public boolean deleted = false;
    //Database stuff
    private boolean updated = false;

    Claim(UUID ownerId, String ownerName, ClaimPosition position) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.position = position;
        this.adminClaim = this.ownerId == null;
    }

    Claim(ClaimPosition position) {
        this(null, null, position);
    }

    //Get
    @Override
    public UUID getOwnerID() {
        return ownerId;
    }

    @Override
    public String getOwnerName() {
        return ownerName;
    }

    @Override
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

    public ClaimPosition getPosition() {
        return position;
    }

    public String getPositionJSON() {
        return JSONEncoding.getJsonFromClaim(position);
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
        return (loc.getBlockY() >= Pueblos.getInstance().getSystems().getSettings().getInt(Settings.SETTING.CLAIM_MAXDEPTH)
                && (position.getLeft() <= loc.getBlockX() && position.getTop() >= loc.getBlockZ()) && //Top Left
                (position.getRight() >= loc.getBlockX() && position.getBottom() <= loc.getBlockZ())); //Bottom Right
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
    @Override
    public void updated() {
        updated = true;
    }

    @Override
    public boolean wasUpdated() {
        return updated;
    }

    @Override
    public void uploaded() {
        updated = false;
    }

    public boolean checkPermLevel(Player p, CLAIM_PERMISSION_LEVEL level) {
        if (level != null)
            switch (level) {
                case OWNER: return isOwner(p) || (isAdminClaim() && PermissionNodes.ADMIN_CLAIM.check(p));
                case MEMBER: return isMember(p);
                default: return true;
            }
        return true;
    }

    public boolean editCorners(Player editor, Location loc_1, Location loc_2) {
        if (!HelperEvent.claimResize(editor, this, editor, loc_1, loc_2).isCancelled()) {
            getPosition().editCorners(loc_1, loc_2);
            updated();
            return true;
        } else
            return false;
    }

    public boolean isAdminClaim() {
        return adminClaim;
    }

    void changeOwner(UUID newOwnerId, boolean save_oldOwner_as_Member) {
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
