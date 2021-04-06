package me.RonanCraft.Pueblos.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimPosition;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class EventListener implements Listener {

    public void load() {
        Pueblos.getInstance().getServer().getPluginManager().registerEvents(this, Pueblos.getInstance());
    }

    //Player Interact
    @EventHandler (priority = EventPriority.HIGHEST)
    void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null)
            return;
        Block block = e.getClickedBlock();
        if (isProtected(block.getLocation())) {
            if (block.getType().name().contains("SIGN")) {
                e.getPlayer().sendMessage("Sign!");
            } else if (block.getType().name().contains("DOOR")) {
                e.getPlayer().sendMessage("Door!");
            } else if (block.getType().name().contains("BUTTON")) {
                e.getPlayer().sendMessage("Button!");
            }
        }
    }

    //Player Block Break
    @EventHandler (priority = EventPriority.HIGHEST)
    void onBreak(BlockBreakEvent e) {
        if (isProtected(e.getPlayer(), e.getBlock().getLocation()))
            e.setCancelled(true);
    }

    //Player Block Place
    @EventHandler (priority = EventPriority.HIGHEST)
    void onPlace(BlockPlaceEvent e) {
        if (isProtected(e.getPlayer(), e.getBlock().getLocation()))
            e.setCancelled(true);
    }

    //Explosion
    @EventHandler (priority = EventPriority.HIGHEST)
    void onExplosion(EntityExplodeEvent e) {
        /*List<Chunk> chunks = new ArrayList<>();
        for (Block block : e.blockList())
            if (!chunks.contains(block.getChunk()))
                chunks.add(block.getChunk());
        for (Chunk chunk : chunks)
            if (isProtected(chunk))
                e.blockList().removeIf(block -> block.getChunk() == chunk);*/
        e.blockList().removeIf(block -> isProtected(block.getLocation()));
    }

    //Pistons
    @EventHandler (priority = EventPriority.HIGHEST)
    void onPiston(BlockPistonExtendEvent e) {
        pistonEvent(e, e.getBlocks(), e.getDirection());
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    void onPiston(BlockPistonRetractEvent e) {
        pistonEvent(e, e.getBlocks(), e.getDirection());
    }

    private void pistonEvent(BlockPistonEvent e, List<Block> blocks, BlockFace dir) {
        /*List<Chunk> chunks = new ArrayList<>();
        chunks.add(e.getBlock().getChunk());
        for (Block block : blocks) {
            Location loc = block.getLocation();
            loc.add(dir.getModX(), dir.getModY(), dir.getModZ());
            //Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.BARRIER, loc.clone().add(0.5, 1.5, 0.5), 1);
            if (!chunks.contains(loc.getChunk()))
                chunks.add(loc.getChunk());
        }
        if (chunks.size() <= 1) //Ignore blocks all in one chunk
            return;

        //Blocks going between chunks
        HashMap<Chunk, Boolean> chunks_protected = new HashMap<>();
        for (Chunk chunk : chunks)
            chunks_protected.put(chunk, isProtected(chunk));

        //Blocks going between unprotected and protected land
        boolean all_match = (chunks_protected.containsValue(true) && !chunks_protected.containsValue(false))
                || (chunks_protected.containsValue(false) && !chunks_protected.containsValue(true));
        if (!all_match) { //Not all match true or false
            System.out.println("Cancelled! " + chunks.toString());
            e.setCancelled(true);
        }*/
    }

    private boolean isProtected(Location loc) {
        return getClaim(loc) != null;
    }

    private boolean isProtected(Player p, Location loc) {
        Claim claim = getClaim(loc);
        return claim != null && !isOwner(p, claim);
    }

    private Claim getClaim(Location loc) {
        for (Map.Entry<UUID, Claim> entry : Pueblos.getInstance().getSystems().getClaimHandler().getClaims()) {
            Claim claim = entry.getValue();
            if (claim.contains(loc))
                return claim;
        }
        return null;
    }

    private boolean isOwner(Player p, Claim claim) {
        return claim.ownerId.equals(p.getUniqueId());
    }
}
