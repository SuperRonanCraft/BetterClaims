package me.RonanCraft.Pueblos.resources.claims;

import org.bukkit.Chunk;

public class ClaimChunk {

    private final int x;
    private final int z;

    public ClaimChunk(Chunk chunk) {
        this.x = chunk.getX();
        this.z = chunk.getZ();
    }

    public ClaimChunk(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }
}
