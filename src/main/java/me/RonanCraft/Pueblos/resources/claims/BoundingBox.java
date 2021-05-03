package me.RonanCraft.Pueblos.resources.claims;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class BoundingBox {

    public int maxX, maxZ;
    public int minX, minZ;

    public BoundingBox(int x1, int z1, int x2, int z2) {
        verify(x1, z1, x2, z2);
    }

    public BoundingBox(Location pos1, Location pos2) {
        this(pos1.getBlockX(), pos1.getBlockZ(), pos2.getBlockX(), pos2.getBlockZ());
    }

    public BoundingBox(Claim claim) {
        this(claim.getLesserBoundaryCorner(), claim.getGreaterBoundaryCorner());
    }

    private void verify(int x1, int z1, int x2, int z2) {
        if (x1 < x2) {
            this.minX = x1;
            this.maxX = x2;
        } else {
            this.minX = x2;
            this.maxX = x1;
        }

        if (z1 < z2) {
            this.minZ = z1;
            this.maxZ = z2;
        } else {
            this.minZ = z2;
            this.maxZ = z1;
        }
    }

    public int getMinX() {
        return this.minX;
    }

    public int getMinZ() {
        return this.minZ;
    }

    public int getMaxX() {
        return this.maxX;
    }

    public int getMaxZ() {
        return this.maxZ;
    }

    public int getWidth() {
        return this.maxZ - this.minZ + 1;
    }

    public int getLength() {
        return this.maxX - this.minX + 1;
    }

    public double getCenterX() {
        return (double)this.minX + (double)this.getLength() / 2.0D;
    }

    public double getCenterZ() {
        return (double)this.minZ + (double)this.getWidth() / 2.0D;
    }

    public Vector getCenter() {
        return new Vector(this.getCenterX(), 0, this.getCenterZ());
    }

    private boolean containsInternal(int minX, int minZ, int maxX, int maxZ) {
        return minX >= this.minX && maxX <= this.maxX && minZ >= this.minZ && maxZ <= this.maxZ;
    }

    public boolean contains(int x, int z) {
        return this.containsInternal(x, z, x, z);
    }

    public boolean contains(Location position) {
        return this.contains(position.getBlockX(), position.getBlockZ());
    }

    public boolean contains(BoundingBox other) {
        return this.containsInternal(other.minX, other.minZ, other.maxX, other.maxZ);
    }

    public boolean intersects(BoundingBox other) {
        return this.minX <= other.maxX && this.maxX >= other.minX && this.minZ <= other.maxZ && this.maxZ >= other.minZ;
    }
}
