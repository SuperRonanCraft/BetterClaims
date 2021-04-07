package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.CLAIM_FLAG;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.*;

public class EventListener implements Listener {

    HashMap<Player, PlayerClaimCreation> claimCreation = new HashMap<>();
    EventBlocks blocks = new EventBlocks(this);
    EventInteract interact = new EventInteract(this);
    EventItemChange itemChange = new EventItemChange(this);
    EventPistons pistons = new EventPistons(this);
    EventDamage damage = new EventDamage(this);

    public void load() {
        Pueblos.getInstance().getServer().getPluginManager().registerEvents(this, Pueblos.getInstance());
    }

    //Player Interact
    @EventHandler (priority = EventPriority.HIGHEST)
    void onInteract(PlayerInteractEvent e) {
        interact.onInteract(e);
    }

    @EventHandler (priority = EventPriority.NORMAL)
    void onInteractCreateClaim(PlayerInteractEvent e) {
        interact.onInteractCreateClaim(e);
    }

    @EventHandler (priority = EventPriority.NORMAL)
    void onItemChange(PlayerItemHeldEvent e) {
        itemChange.onItemChange(e);
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
        pistons.onPiston(e);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    void onPiston(BlockPistonRetractEvent e) {
        pistons.onPiston(e);
    }

    //PvP
    @EventHandler (priority = EventPriority.HIGHEST)
    void onDamage(EntityDamageByEntityEvent e) {
        damage.onDamage(e);
    }

    //Blocks
    @EventHandler (priority = EventPriority.HIGHEST)
    void onBreak(BlockBreakEvent e) {
        blocks.onBreak(e);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    void onPlace(BlockPlaceEvent e) {
        blocks.onPlace(e);
    }

    boolean isProtected(Location loc) {
        return getClaim(loc) != null;
    }

    boolean isProtected(Player p, Location loc) {
        Claim claim = getClaim(loc);
        //if (claim != null && isOwner(p, claim))
        //    Visualization.addClaimElements(claim, p.getLocation().getBlockY(), VisualizationType.Claim, p.getLocation()).apply(p);
        return claim != null && !isOwner(p, claim);
    }

    Claim getClaim(Location loc) {
        for (Claim claim : Pueblos.getInstance().getSystems().getClaimHandler().getClaims()) {
            if (claim.contains(loc))
                return claim;
        }
        return null;
    }

    boolean isOwner(Player p, Claim claim) {
        return claim.ownerId.equals(p.getUniqueId());
    }
}
