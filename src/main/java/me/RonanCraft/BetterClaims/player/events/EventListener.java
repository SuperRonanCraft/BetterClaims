package me.RonanCraft.BetterClaims.player.events;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.player.data.PlayerData;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_MODE;
import me.RonanCraft.BetterClaims.resources.helper.HelperPlayer;
import me.RonanCraft.BetterClaims.resources.messages.MessagesCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.PortalCreateEvent;

public class EventListener implements Listener {

    //Events
    EventBlocks blocks = new EventBlocks();
    EventInteract interact = new EventInteract();
    EventItemChange itemChange = new EventItemChange(this);
    EventPistons pistons = new EventPistons();
    EventExplosion explosion = new EventExplosion();
    EventDamage damage = new EventDamage();
    EventClick click = new EventClick();
    EventClose close = new EventClose();
    EventJoinLeave joinLeave = new EventJoinLeave();
    EventItems items = new EventItems();
    EventFallingBlock fallingBlock = new EventFallingBlock();
    EventPortal portal = new EventPortal();
    EventMove move = new EventMove();

    public void load(boolean reload) {
        if (!reload)
            BetterClaims.getInstance().getServer().getPluginManager().registerEvents(this, BetterClaims.getInstance());
        interact.load();
        items.load();
    }

    public void toggleAdminClaim(Player p) { //Can fail if not having the claim item equipped and has no locations
        PlayerData data = HelperPlayer.getData(p);
        if (data.getClaimInteraction() != null) {
            PlayerClaimInteraction interaction = data.getClaimInteraction();
            if (interaction.locations.isEmpty()) {
                if (interaction.mode != CLAIM_MODE.CREATE_ADMIN) {
                    interaction.mode = CLAIM_MODE.CREATE_ADMIN;
                    MessagesCore.CLAIM_MODE_ENABLED_ADMIN.send(p);
                } else {
                    interaction.mode = CLAIM_MODE.CREATE;
                    MessagesCore.CLAIM_MODE_DISABLED_ADMIN.send(p);
                }
            } else
                MessagesCore.CLAIM_MODE_FAILED_LOCATION.send(p);
        } else
            MessagesCore.CLAIM_MODE_FAILED_ITEM.send(p);
    }

    //Player Interact
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onInteract(PlayerInteractEvent e) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            interact.onInteract(e);
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    void onInteractCreateClaim(PlayerInteractEvent e) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            interact.onInteractCreateClaim(e);
        }
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.NORMAL)
    void onItemChange(PlayerItemHeldEvent e) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            itemChange.onItemChange(e);
        }
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
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onExplosion(EntityExplodeEvent e) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            explosion.onExplosion(e);
        }
    }

    //Pistons
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onPiston(BlockPistonExtendEvent e) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            pistons.onPiston(e);
        }
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onPiston(BlockPistonRetractEvent e) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            pistons.onPiston(e);
        }
    }

    //PvP
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onDamage(EntityDamageByEntityEvent e) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            damage.damageEntity(e);
        }
    }
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onDamage(HangingBreakByEntityEvent e) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            damage.damageHanging(e);
        }
    }

    //Blocks
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onBreak(BlockBreakEvent e) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            blocks.onBreak(e);
        }
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onPlace(BlockPlaceEvent e) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            blocks.onPlace(e);
        }
    }

    @EventHandler
    void onSignChange(SignChangeEvent e) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            blocks.onSignChange(e);
        }
    }

    //Click Inventory
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    void onClick(InventoryClickEvent e) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            click.click(e);
        }
    }

    //Close Inventory
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    void onClose(InventoryCloseEvent e) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            close.exit(e);
        }
    }

    //Player Join/Quit Event
    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            joinLeave.join(e);
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent e) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            interact.forget(e.getPlayer());
            itemChange.forget(e.getPlayer());
            joinLeave.leave(e);
        }
    }

    //Falling Blocks
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    private void fallingBlock(EntityChangeBlockEvent e) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            fallingBlock.onEntityChangeBLock(e);
        }
    }

    //Nether Portal
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    private void portal(PortalCreateEvent e) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            portal.onPortal(e);
        }
    }

    //Move
    @EventHandler(priority = EventPriority.MONITOR)
    private void onMove(PlayerMoveEvent e) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            move.onMove(e);
        }
    }
}
