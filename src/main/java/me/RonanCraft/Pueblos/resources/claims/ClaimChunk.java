package me.RonanCraft.Pueblos.resources.claims;

public class ClaimChunk {

    private final int x;
    private final int z;

    ClaimChunk(int x, int z) {
        this.x = x;
        this.z = z;
    }

    int getX() {
        return x;
    }

    int getZ() {
        return z;
    }
}
