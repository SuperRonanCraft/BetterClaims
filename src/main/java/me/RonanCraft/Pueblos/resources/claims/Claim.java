package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.resources.tools.JSONEncoding;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;
import java.util.UUID;

public class Claim {
    public final UUID ownerId;
    public final String ownerName;
    private ClaimPosition position;
    private List<ClaimMember> members;

    Claim(UUID ownerId, String ownerName, ClaimPosition position) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.position = position;
    }

    public ClaimPosition getPosition() {
        return position;
    }

    public String getPositionJSON() {
        return JSONEncoding.getJsonFromClaim(position);
    }

    public boolean contains(Location loc) {
        /*if (position.getLeft() <= loc.getBlockX())
            System.out.println("Left");
        if (position.getTop() >= loc.getBlockZ())
            System.out.println("Top");
        if (position.getRight() >= loc.getBlockX())
            System.out.println("Right");
        if (position.getBottom() <= loc.getBlockZ())
            System.out.println("Bottom");*/
        return (position.getLeft() <= loc.getBlockX() && position.getTop() >= loc.getBlockZ()) && //Top Left
                (position.getRight() >= loc.getBlockX() && position.getBottom() <= loc.getBlockZ()); //Bottom Right
    }
}
