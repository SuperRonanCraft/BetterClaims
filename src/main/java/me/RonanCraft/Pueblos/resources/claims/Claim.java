package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.resources.tools.JSONEncoding;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Claim {
    public final UUID ownerId;
    public final String ownerName;
    public long claimId; //ID given by the database
    //Claim Information
    private final ClaimPosition position;
    private final ClaimFlags flags = new ClaimFlags(this);
    private final List<ClaimMember> members = new ArrayList<>();
    private final List<ClaimRequest> requests = new ArrayList<>();
    private String name;
    //Database stuff
    private boolean updated = false;

    Claim(UUID ownerId, String ownerName, ClaimPosition position) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.position = position;
    }

    //Is
    public boolean isOwner(Player p) {
        return p.getUniqueId().equals(ownerId);
    }

    public boolean isMember(Player p) {
        if (p.getUniqueId().equals(ownerId))
            return true;
        for (ClaimMember member : members)
            if (member.getId().equals(p.getUniqueId()))
                return true;
        return false;
    }

    //Add
    public void addMember(ClaimMember member, boolean update) {
        if (update) updated();
        if (member != null) members.add(member);
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

    public Location getLesserBoundaryCorner() {
        return new Location(position.getWorld(), position.getLeft(), 0, position.getBottom());
    }

    public Location getGreaterBoundaryCorner() {
        return new Location(position.getWorld(), position.getRight(), 0, position.getTop());
    }

    public String getName() {
        return name != null ? name : ownerName;
    }

    public List<ClaimMember> getMembers() {
        return members;
    }

    public ClaimMember getMember(Player player) {
        for (ClaimMember member : members)
            if (member.uuid == player.getUniqueId())
                return member;
        return null;
    }

    public List<ClaimRequest> getRequests() {
        return requests;
    }

    //Checks
    public boolean contains(Location loc) {
        return (position.getLeft() <= loc.getBlockX() && position.getTop() >= loc.getBlockZ()) && //Top Left
                (position.getRight() >= loc.getBlockX() && position.getBottom() <= loc.getBlockZ()); //Bottom Right
    }

    public boolean hasRequestFrom(Player p) {
        for (ClaimRequest request : requests)
            if (request.id == p.getUniqueId())
                return true;
        return false;
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
}
