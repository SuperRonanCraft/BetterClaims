package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.resources.tools.JSONEncoding;
import org.bukkit.Location;
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
    private final ClaimFlags flags = new ClaimFlags();
    private final List<ClaimMember> members = new ArrayList<>();
    private String name;
    //Database stuff
    private boolean updated = false;

    Claim(UUID ownerId, String ownerName, ClaimPosition position) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.position = position;
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

    public boolean contains(Location loc) {
        return (position.getLeft() <= loc.getBlockX() && position.getTop() >= loc.getBlockZ()) && //Top Left
                (position.getRight() >= loc.getBlockX() && position.getBottom() <= loc.getBlockZ()); //Bottom Right
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

    public boolean isMember(Player p) {
        if (p.getUniqueId().equals(ownerId))
            return true;
        for (ClaimMember member : members)
            if (member.getId().equals(p.getUniqueId()))
                return true;
        return false;
    }

    public boolean isOwner(Player p) {
        return p.getUniqueId().equals(ownerId);
    }

    public void addMember(ClaimMember member) {
        if (member != null)
            members.add(member);
    }

    public List<ClaimMember> getMembers() {
        return members;
    }

    public void updated() {
        updated = true;
    }

    public ClaimMember getMember(Player player) {
        for (ClaimMember member : members)
            if (member.uuid == player.getUniqueId())
                return member;
        return null;
    }
}
