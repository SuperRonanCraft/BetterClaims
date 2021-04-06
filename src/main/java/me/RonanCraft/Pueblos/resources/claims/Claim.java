package me.RonanCraft.Pueblos.resources.claims;

import org.bukkit.Chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Claim {
    public final UUID ownerId;
    public final String ownerName;
    private final List<Chunk> chunks = new ArrayList<>();

    public Claim(UUID ownerId, String ownerName) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
    }

    public void addChunk(Chunk chunk) {
        chunks.add(chunk);
    }

    public List<Chunk> getChunks() {
        return chunks;
    }
}
