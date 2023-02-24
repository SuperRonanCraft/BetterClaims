package me.RonanCraft.BetterClaims.player.events;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.player.data.PlayerData;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_MODE;
import me.RonanCraft.BetterClaims.resources.messages.MessagesCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.PortalCreateEvent;

public class EventListener implements Listener {

    //Events
    EventBlocks blocks = new EventBlocks();
    EventInteract interact = new EventInteract(this);
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
    EventMove move = new EventMove(this);

    public void load(boolean reload) {
        if (!reload)
            BetterClaims.getInstance().getServer().getPluginManager().registerEvents(this, BetterClaims.getInstance());
        interact.load();
        items.load();
    }

    public void toggleAdminClaim(Player p) { //Can fail if not having the claim item equipped and has no locations
        if (getPlayerData(p).getClaimInteraction() != null) {
            PlayerClaimInteraction interaction = getPlayerData(p).getClaimInteraction();
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
        interact.onInteract(e);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    void onInteractCreateClaim(PlayerInteractEvent e) {
        interact.onInteractCreateClaim(e);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.NORMAL)
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
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onExplosion(EntityExplodeEvent e) {
        explosion.onExplosion(e);
    }

    //Pistons
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onPiston(BlockPistonExtendEvent e) {
        pistons.onPiston(e);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onPiston(BlockPistonRetractEvent e) {
        pistons.onPiston(e);
    }

    //PvP
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onDamage(EntityDamageByEntityEvent e) {
        damage.onDamage(e);
    }

    //Blocks
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onBreak(BlockBreakEvent e) {
        blocks.onBreak(e);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onPlace(BlockPlaceEvent e) {
        blocks.onPlace(e);
    }

    @EventHandler
    void onSignChange(SignChangeEvent e) {
        blocks.onSignChange(e);
    }

    //Click Inventory
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    void onClick(InventoryClickEvent e) {
        click.click(e);
    }

    //Close Inventory
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    void onClose(InventoryCloseEvent e) {
        close.exit(e);
    }

    //Player Join/Quit Event
    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        joinLeave.join(e);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent e) {
        joinLeave.leave(e);
    }

    //Falling Blocks
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    private void fallingBlock(EntityChangeBlockEvent e) {
        fallingBlock.onEntityChangeBLock(e);
    }

    //Nether Portal
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    private void portal(PortalCreateEvent e) {
        portal.onPortal(e);
    }

    //Move
    @EventHandler(priority = EventPriority.MONITOR)
    private void onMove(PlayerMoveEvent e) {
        move.onMove(e);
    }

    PlayerData getPlayerData(Player p) {
        return BetterClaims.getInstance().getPlayerData(p);
    }
}
