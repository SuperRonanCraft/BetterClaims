package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.resources.tools.JSONEncoding;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Claim {
    public final UUID ownerId;
    public final String ownerName;
    public final World world;
    private final List<Chunk> chunks = new ArrayList<>();

    public Claim(UUID ownerId, String ownerName, World world) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.world = world;
    }

    public void addChunk(Chunk chunk) {
        chunks.add(chunk);
    }

    public void addChunk(List<Chunk> chunk) {
        chunks.addAll(chunk);
    }

    public List<Chunk> getChunks() {
        return chunks;
    }

    public String getChunksJSON() {
        return JSONEncoding.getJsonFromChunk(chunks);
    }
}
