package me.RonanCraft.Pueblos.player;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.player.command.types.CmdCreate;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimHandler;
import me.RonanCraft.Pueblos.resources.claims.ClaimPosition;
import me.RonanCraft.Pueblos.resources.files.msgs.Message;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.*;

public class EventListener implements Listener {

    HashMap<Player, PlayerClaimCreation> claimCreation = new HashMap<>();

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

    @EventHandler (priority = EventPriority.NORMAL)
    void onInteractCreateClaim(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null || e.getItem() == null || e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        else if (!e.getItem().getType().equals(Material.GOLDEN_SHOVEL))
            return;
        Player p = e.getPlayer();
        if (!claimCreation.containsKey(p))
            claimCreation.put(p, new PlayerClaimCreation(p));
        PlayerClaimCreation claimCreation = this.claimCreation.get(p);
        Location loc = e.getClickedBlock().getLocation();
        if (!claimCreation.locked && claimCreation.addLocation(loc)) {
            List<Location> corners = claimCreation.locations;
            if (corners.size() >= 2) {
                ClaimHandler handler = Pueblos.getInstance().getSystems().getClaimHandler();
                Claim claim = handler.claimCreate(p.getUniqueId(), p.getName(), new ClaimPosition(p.getWorld(), corners.get(0), corners.get(1)));
                if (claim != null) {
                    Messages.core.sms(p, "Claim Created!");
                    CmdCreate.showCorners(p, claim);
                } else
                    Messages.core.sms(p, "Claim Error!");
                claimCreation.lock(); //Lock us from making another claim using this item
            } else {
                CmdCreate.showCorners(p, e.getClickedBlock());
            }
        }
        e.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.NORMAL)
    void onItemChange(PlayerItemHeldEvent e) {
        claimCreation.remove(e.getPlayer());
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
        List<Location> locations = new ArrayList<>();
        locations.add(e.getBlock().getLocation());
        blocks: for (Block block : blocks) {
            Location loc = block.getLocation();
            loc.add(dir.getModX(), dir.getModY(), dir.getModZ());
            //Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.BARRIER, loc.clone().add(0.5, 1.5, 0.5), 1);
            for (Location _loc : locations)
                if (_loc.getBlockX() == loc.getBlockX() && _loc.getBlockZ() == loc.getBlockZ()) {
                    break blocks;
                }
            locations.add(loc);
        }
        if (locations.size() <= 1) //Ignore blocks all in one chunk
            return;

        //Blocks going between chunks
        HashMap<Location, Boolean> locations_protected = new HashMap<>();
        for (Location loc : locations)
            locations_protected.put(loc, isProtected(loc));

        //Blocks going between unprotected and protected land
        boolean all_match = (locations_protected.containsValue(true) && !locations_protected.containsValue(false))
                || (locations_protected.containsValue(false) && !locations_protected.containsValue(true));
        if (!all_match) { //Not all match true or false
            e.setCancelled(true);
        }
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
