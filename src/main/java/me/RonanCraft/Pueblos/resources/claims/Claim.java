package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.resources.tools.JSONEncoding;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

public class Claim {
    public final UUID ownerId;
    public final String ownerName;
    public final World world;
    private ClaimPosition position;

    public Claim(UUID ownerId, String ownerName, World world) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.world = world;
    }

    public void setPosition(ClaimPosition position) {
        this.position = position;
    }

    public ClaimPosition getPosition() {
        return position;
    }

    public String getChunksJSON() {
        return null;//JSONEncoding.getJsonFromChunk(position);
    }

    public boolean contains(Location loc) {
        return (getLeft() <= loc.getBlockX() && getTop() <= loc.getBlockZ()) && //Top Left
                (getRight() >= loc.getBlockX() && getBottom() >= loc.getBlockZ()); //Bottom Right
    }

    public int getLeft() {
        return Math.min(position.getX_1(), position.getX_2());
    }

    public int getTop() {
        return Math.max(position.getZ_1(), position.getZ_2());
    }

    public int getRight() {
        return Math.max(position.getX_1(), position.getX_2());
    }

    public int getBottom() {
        return Math.min(position.getZ_1(), position.getZ_2());
    }
}
