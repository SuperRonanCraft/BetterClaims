package me.RonanCraft.Pueblos.resources.claims;

import org.bukkit.Location;
import org.bukkit.World;

public class ClaimPosition {

    private final World world;
    private int x_1;
    private int z_1;
    private int x_2;
    private int z_2;

    public ClaimPosition(World world, int x_1, int z_1, int x_2, int z_2) {
        this.world = world;
        this.x_1 = x_1;
        this.z_1 = z_1;
        this.x_2 = x_2;
        this.z_2 = z_2;
    }

    public ClaimPosition(World world, Location loc_1, Location loc_2) {
        this(world, loc_1.getBlockX(), loc_1.getBlockZ(), loc_2.getBlockX(), loc_2.getBlockZ());
    }

    public World getWorld() {
        return world;
    }

    public int getX_1() {
        return x_1;
    }

    public int getZ_1() {
        return z_1;
    }

    public int getX_2() {
        return x_2;
    }

    public int getZ_2() {
        return z_2;
    }

    public int getLeft() {
        return Math.min(getX_1(), getX_2());
    }

    public int getTop() {
        return Math.max(getZ_1(), getZ_2());
    }

    public int getRight() {
        return Math.max(getX_1(), getX_2());
    }

    public int getBottom() {
        return Math.min(getZ_1(), getZ_2());
    }

    public Location getLesserBoundaryCorner() {
        return new Location(getWorld(), getLeft(), 0, getBottom());
    }

    public Location getGreaterBoundaryCorner() {
        return new Location(getWorld(), getRight(), 0, getTop());
    }

    public Location getLocation() {
        return new Location(getWorld(), getLeft() + 0.5, getWorld().getHighestBlockYAt(getLeft(), getTop()) + 1.25, getTop() + 0.5);
    }

    public boolean isCorner(Location loc) {
        return (loc.getBlockX() == getLeft() || loc.getBlockX() == getRight()) && (loc.getBlockZ() == getTop() || loc.getBlockZ() == getBottom());
    }

    public Location getCorner(CLAIM_CORNER corner) {
        switch (corner) {
            case TOP_LEFT: return new Location(getWorld(), getLeft(), 0, getTop());
            case TOP_RIGHT: return new Location(getWorld(), getRight(), 0, getTop());
            case BOTTOM_LEFT: return new Location(getWorld(), getLeft(), 0, getBottom());
            case BOTTOM_RIGHT: return new Location(getWorld(), getRight(), 0, getBottom());
        }
        return null;
    }

    public CLAIM_CORNER getCorner(Location loc) {
        for (CLAIM_CORNER corner : CLAIM_CORNER.values()) {
            Location cornerLoc = getCorner(corner);
            if (cornerLoc.getBlockX() == loc.getBlockX() && cornerLoc.getBlockZ() == loc.getBlockZ())
                return corner;
        }
        return null;
    }

    void editCorners(Location loc_1, Location loc_2) {
        this.x_1 = loc_1.getBlockX();
        this.z_1 = loc_1.getBlockZ();
        this.x_2 = loc_2.getBlockX();
        this.z_2 = loc_2.getBlockZ();
    }

    public enum CLAIM_CORNER {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT;

        public CLAIM_CORNER opposite() {
            switch (this) {
                case TOP_LEFT: return BOTTOM_RIGHT;
                case TOP_RIGHT: return BOTTOM_LEFT;
                case BOTTOM_LEFT: return TOP_RIGHT;
                case BOTTOM_RIGHT: return TOP_LEFT;
            }
            return null;
        }
    }
}
