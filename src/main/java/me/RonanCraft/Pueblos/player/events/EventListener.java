package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.*;

public class EventListener implements Listener {

    HashMap<Player, PlayerClaimInteraction> claimInteraction = new HashMap<>();
    EventBlocks blocks = new EventBlocks(this);
    EventInteract interact = new EventInteract(this);
    EventItemChange itemChange = new EventItemChange(this);
    EventPistons pistons = new EventPistons(this);
    EventExplosion explosion = new EventExplosion(this);
    EventDamage damage = new EventDamage(this);
    EventClick click = new EventClick();
    EventClose close = new EventClose();
    EventJoin join = new EventJoin();
    EventItems items = new EventItems(this);

    public void load(boolean reload) {
        if (!reload)
            Pueblos.getInstance().getServer().getPluginManager().registerEvents(this, Pueblos.getInstance());
        interact.load();
        items.load();
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

    //Items OWN LISTENER EVENT
    /*@EventHandler
    void event(EntityPickupItemEvent e) {
        items.onPickup(e);
    }

    @EventHandler
    void event(PlayerDeathEvent e) {
        items.onDrop(e);
    }*/

    //Explosion
    @EventHandler (priority = EventPriority.HIGHEST)
    void onExplosion(EntityExplodeEvent e) {
        explosion.onExplosion(e);
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

    //Click Inventory
    @EventHandler (priority = EventPriority.HIGH)
    void onClick(InventoryClickEvent e) {
        click.click(e);
    }

    //Close Inventory
    @EventHandler (priority = EventPriority.HIGH)
    void onClose(InventoryCloseEvent e) {
        close.exit(e);
    }

    //Player Join Event
    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        join.event(e);
    }

    //Tools
    boolean isProtected(Location loc) {
        return getClaim(loc) != null;
    }

    void sendNotAllowedMsg(Player p, Claim claim) {
        p.sendMessage("You are not allowed to do this here! If you believe you should, send a request to join this claim!");
    }

    Claim getClaim(Location loc) {
        for (Claim claim : Pueblos.getInstance().getSystems().getClaimHandler().getClaims()) {
            if (claim.contains(loc))
                return claim;
        }
        return null;
    }
}
