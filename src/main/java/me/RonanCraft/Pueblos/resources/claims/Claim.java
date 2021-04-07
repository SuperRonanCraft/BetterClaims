package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.resources.tools.JSONEncoding;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;
import java.util.UUID;

public class Claim {
    public final UUID ownerId;
    public final String ownerName;
    public long claimId; //ID given by the database
    private final ClaimPosition position;
    private final ClaimFlags claimFlags = new ClaimFlags();
    private List<ClaimMember> members;

    Claim(UUID ownerId, String ownerName, ClaimPosition position) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.position = position;
    }

    public ClaimFlags getFlags() {
        return claimFlags;
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
}
