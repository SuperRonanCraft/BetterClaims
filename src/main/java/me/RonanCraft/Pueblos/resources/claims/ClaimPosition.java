package me.RonanCraft.Pueblos.resources.claims;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class ClaimPosition {

    private final int x_1;
    private final int z_1;
    private final int x_2;
    private final int z_2;

    public ClaimPosition(int x_1, int z_1, int x_2, int z_2) {
        this.x_1 = x_1;
        this.z_1 = z_1;
        this.x_2 = x_2;
        this.z_2 = z_2;
    }

    public ClaimPosition(Location loc_1, Location loc_2) {
        this(loc_1.getBlockX(), loc_1.getBlockZ(), loc_2.getBlockX(), loc_2.getBlockZ());
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
}
