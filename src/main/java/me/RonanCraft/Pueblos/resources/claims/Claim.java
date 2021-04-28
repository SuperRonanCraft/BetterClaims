package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.Settings;
import me.RonanCraft.Pueblos.resources.tools.HelperEvent;
import me.RonanCraft.Pueblos.resources.tools.JSONEncoding;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class Claim implements ClaimInfo {
    private final UUID ownerId;
    private final String ownerName;
    public long claimId; //ID given by the database
    //Claim Information
    private final boolean adminClaim;
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
        return members.isMember(p);
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
    public OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(ownerId);
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
        return members.getMember(p);
    }

    public boolean canBuild(Player p) {
        ClaimMember member = getMember(p);
        return member != null;
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
        switch (level) {
            case OWNER: return isOwner(p);
            case MEMBER: return isMember(p);
            case NONE: return true;
        }
        return false;
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
}
