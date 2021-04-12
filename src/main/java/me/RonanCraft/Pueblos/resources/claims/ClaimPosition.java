package me.RonanCraft.Pueblos.resources.claims;

import org.bukkit.Location;
import org.bukkit.World;

public class ClaimPosition {

    private final World world;
    private final int x_1;
    private final int z_1;
    private final int x_2;
    private final int z_2;

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
}
