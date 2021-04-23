package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.Settings;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EventItems implements Listener {

    private final EventListener listener;
                        //Item, Player
    private final HashMap<UUID, UUID> items = new HashMap<>();
    private final HashMap<UUID, Integer> schedule = new HashMap<>();

    EventItems(EventListener listener) {
        this.listener = listener;
    }

    void load() {
        if (Pueblos.getInstance().getSystems().getSettings().getBoolean(Settings.SETTING.PLAYER_PROTECTDEATHDROP))
            Bukkit.getPluginManager().registerEvents(this, Pueblos.getInstance());
        else
            HandlerList.unregisterAll(this);
    }

    @EventHandler
    private void onDespawn(ItemDespawnEvent e) {
        remove(e.getEntity().getUniqueId());
    }

    //Stop picking up items from other claims
    @EventHandler
    private void onPickup(EntityPickupItemEvent e) {
        if (e.isCancelled())
            return;
        if (e.getEntity() instanceof Player && items.containsKey(e.getItem().getUniqueId())) { //This item is a death item
            if (!e.getEntity().getUniqueId().equals(items.get(e.getItem().getUniqueId()))) //Player who died matches this items owner
                e.setCancelled(true);
            remove(e.getItem().getUniqueId());
            return; //Let players who died in another claim pick up their items
        }
        Claim claim = listener.getClaim(e.getItem().getLocation());
        if (claim != null && (!(e.getEntity() instanceof Player) || !claim.isMember((Player) e.getEntity())))
            e.setCancelled(true);
    }

    //Disallow other players from picking up dead players items
    @EventHandler
    private void onDrop(PlayerDeathEvent e) {
        List<ItemStack> items = e.getDrops();
        for (ItemStack item : items) {
            Item drop = Objects.requireNonNull(e.getEntity().getLocation().getWorld()).dropItem(e.getEntity().getLocation(), item);
            UUID item_id = drop.getUniqueId();
            this.items.put(item_id, e.getEntity().getUniqueId());
        }
        e.getDrops().clear(); //Disallow dropping items normally
    }

    //Remove/Cancel schedule and items to protect
    private void remove(UUID item_id) {
        if (schedule.containsKey(item_id))
            if (Bukkit.getScheduler().isQueued(schedule.get(item_id)))
                Bukkit.getScheduler().cancelTask(schedule.get(item_id));
        schedule.remove(item_id);
        items.remove(item_id);
    }
}
