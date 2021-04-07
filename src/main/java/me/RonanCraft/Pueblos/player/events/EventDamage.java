package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.CLAIM_FLAG;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;

public class EventDamage {

    private final EventListener listener;
    private final HashMap<Entity, Integer> pvpCooldown = new HashMap<Entity, Integer>();

    EventDamage(EventListener listener) {
        this.listener = listener;
    }

    void onDamage(EntityDamageByEntityEvent e) {
        final Entity damager = e.getDamager();
        final Entity damaged = e.getEntity();

        Long currentTime = System.currentTimeMillis() / 1000;
        if (pvpCooldown.containsKey(damaged)) {
            cooldown(damaged);
            e.setCancelled(true); //In a cooldown till we check the claims again
            return;
        } else if (pvpCooldown.containsKey(damager)) {
            cooldown(damager);
            e.setCancelled(true); //In a cooldown till we check the claims again
            return;
        }
        if (damager instanceof Player && damaged instanceof Player) {
            Claim claim = listener.getClaim(damager.getLocation());
            if (claim == null)
                claim = listener.getClaim(damaged.getLocation());
            if (claim != null && !((Boolean) claim.getFlags().getFlag(CLAIM_FLAG.PVP)))
                e.setCancelled(true);
            cooldown(damaged);
            cooldown(damager);
        }
    }

    void cooldown(Entity e) {
        if (pvpCooldown.containsKey(e))
            Bukkit.getScheduler().cancelTask(pvpCooldown.get(e));
        pvpCooldown.put(e, Bukkit.getScheduler().scheduleSyncDelayedTask(Pueblos.getInstance(), () -> {
            pvpCooldown.remove(e);
        }, 20L * 10));
    }
}
