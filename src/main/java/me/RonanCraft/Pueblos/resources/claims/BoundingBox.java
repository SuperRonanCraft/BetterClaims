package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.Settings;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_CORNER;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * Most credit for this class, the heart of Pueblos goes to `GriefPrevention` by BigScary
 * https://github.com/TechFortress/GriefPrevention/blob/master/src/main/java/me/ryanhamshire/GriefPrevention/util/BoundingBox.java
 */

public class BoundingBox {

    private int maxX, maxZ;
    private final int maxY;
    private int minX, minZ;
    private final int minY;
    private final World world;

    public BoundingBox(World world, int x1, int z1, int x2, int z2) {
        this.world = world;
        setMinMax(x1, z1, x2, z2);
        maxY = 255;//world != null ? world.getMaxHeight() : 255;
        minY = Pueblos.getInstance().getSettings().getInt(Settings.SETTING.CLAIM_MAXDEPTH);
    }

    public BoundingBox(Location loc_1, Location loc_2) {
        this(loc_1.getWorld(),loc_1.getBlockX(), loc_1.getBlockZ(), loc_2.getBlockX(), loc_2.getBlockZ());
    }

    public BoundingBox(World world, Vector loc_1, Vector loc_2) {
        this(world, loc_1.getBlockX(), loc_1.getBlockZ(), loc_2.getBlockX(), loc_2.getBlockZ());
    }


    public int getMaxX() {
        return maxX;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getLeft() {
        return Math.min(getMaxX(), getMinX());
    }

    public int getTop() {
        return Math.max(getMaxZ(), getMinZ());
    }

    public int getRight() {
        return Math.max(getMaxX(), getMinX());
    }

    public int getBottom() {
        return Math.min(getMaxZ(), getMinZ());
    }

    public boolean isCorner(Location loc) {
        return (loc.getBlockX() == getLeft() || loc.getBlockX() == getRight()) && (loc.getBlockZ() == getTop() || loc.getBlockZ() == getBottom());
    }

    public Vector getCorner(CLAIM_CORNER corner) {
        switch (corner) {
            case TOP_LEFT: return new Vector(getLeft(), 0, getTop());
            case TOP_RIGHT: return new Vector(getRight(), 0, getTop());
            case BOTTOM_LEFT: return new Vector(getLeft(), 0, getBottom());
            case BOTTOM_RIGHT: return new Vector(getRight(), 0, getBottom());
        }
        return null;
    }

    public CLAIM_CORNER getCorner(Vector loc) {
        for (CLAIM_CORNER corner : CLAIM_CORNER.values()) {
            Vector cornerLoc = getCorner(corner);
            if (cornerLoc.getBlockX() == loc.getBlockX() && cornerLoc.getBlockZ() == loc.getBlockZ())
                return corner;
        }
        return null;
    }

    void editCorners(Vector loc_1, Vector loc_2) {
        int x1 = loc_1.getBlockX();
        int z1 = loc_1.getBlockZ();
        int x2 = loc_2.getBlockX();
        int z2 = loc_2.getBlockZ();
        setMinMax(x1, z1, x2, z2);
    }

    //Makes sure the max are max's and min are min's
    private void setMinMax(int x1, int z1, int x2, int z2) {
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

    //Mostly used for animations
    public int getWidth() {
        return this.maxZ - this.minZ + 1;
    }

    public int getLength() {
        return this.maxX - this.minX + 1;
    }

    public double getCenterX() {
        return this.minX + (double)this.getLength() / 2;
    }

    public double getCenterZ() {
        return this.minZ + (double)this.getWidth() / 2;
    }

    public Vector getCenter() {
        return new Vector(this.getCenterX(), 0, this.getCenterZ());
    }

    //Check to see if all corners of the box are bound within this box
    private boolean containsInternal(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return minX >= this.minX && maxX <= this.maxX && minY >= this.minY && maxY <= this.maxY && minZ >= this.minZ && maxZ <= this.maxZ;
    }

    private boolean contains(int x, int y, int z) {
        return this.containsInternal(x, y, z, x, y, z);
    }

    public boolean contains(Location position) {
        return this.contains(position.getBlockX(), position.getBlockY(), position.getBlockZ());
    }

    public boolean contains(Vector position) {
        return this.contains(position.getBlockX(), position.getBlockY(), position.getBlockZ());
    }

    /**
     * @param leniency Used to expand the bounding box just to check if we are withing x radius around the box
     **/
    private boolean contains(int x, int y, int z, int leniency) {
        return this.containsInternal(x - leniency, y - leniency, z - leniency, x + leniency, y + leniency, z + leniency);
    }

    public boolean contains(Location position, int leniency) {
        return this.contains(position.getBlockX(), position.getBlockY(), position.getBlockZ(), leniency);
    }

    //Use for checking if a sub claim is inside a master claim
    public boolean contains(BoundingBox otherBox) {
        return this.containsInternal(otherBox.minX, otherBox.minY, otherBox.minZ, otherBox.maxX, otherBox.maxY, otherBox.maxZ);
    }

    /**
     * Checks if the bounding box intersects another bounding box.
     *
     * @param other the other bounding box
     * @return true if the specified positions are inside the bounding box
     */
    public boolean intersects(BoundingBox other) {
        // For help visualizing test cases, try https://silentmatt.com/rectangle-intersection/
        return this.minX <= other.maxX && this.maxX >= other.minX
                && this.minY <= other.maxY && this.maxY >= other.minY
                && this.minZ <= other.maxZ && this.maxZ >= other.minZ;
    }

    public World getWorld() {
        return world;
    }
}
