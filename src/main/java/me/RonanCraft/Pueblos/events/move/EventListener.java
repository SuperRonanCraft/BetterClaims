package me.RonanCraft.Pueblos.events.move;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EventListener implements Listener {

    public void load() {
        Pueblos.getInstance().getServer().getPluginManager().registerEvents(this, Pueblos.getInstance());
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    void onBreak(BlockBreakEvent e) {
        if (isProtected(e.getBlock().getChunk()))
            e.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    void onPlace(BlockPlaceEvent e) {
        if (isProtected(e.getBlock().getChunk()))
            e.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    void onExplosion(EntityExplodeEvent e) {
        List<Chunk> chunks = new ArrayList<>();
        for (Block block : e.blockList())
            if (!chunks.contains(block.getChunk()))
                chunks.add(block.getChunk());
        for (Chunk chunk : chunks)
            if (isProtected(chunk)) {
                e.blockList().removeIf(block -> block.getChunk() == chunk);
            }
    }

    private boolean isProtected(Chunk chunk) {
        for (Map.Entry<UUID, Claim> entry : Pueblos.getInstance().getSystems().getClaimHandler().getClaims()) {
            Claim claim = entry.getValue();
            if (claim.getChunks().contains(chunk)) {
                return true;
            }
        }
        return false;
    }
}
