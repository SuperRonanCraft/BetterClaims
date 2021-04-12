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
    private final HashMap<Entity, Integer> damageCooldown = new HashMap<Entity, Integer>();

    EventDamage(EventListener listener) {
        this.listener = listener;
    }

    //Damage Entity (Mobs and Players)
    void onDamage(EntityDamageByEntityEvent e) {
        final Entity damager = e.getDamager();
        final Entity damaged = e.getEntity();

        if (damageCooldown.containsKey(damaged)) {
            cooldown(damaged);
            e.setCancelled(true); //In a cooldown till we check the claims again
            return;
        } else if (damageCooldown.containsKey(damager)) {
            cooldown(damager);
            e.setCancelled(true); //In a cooldown till we check the claims again
            return;
        }

        Claim claim = listener.getClaim(damager.getLocation());
        if (claim == null)
            claim = listener.getClaim(damaged.getLocation());
        if (claim != null) {
            if (damaged instanceof Player && damager instanceof Player) //PvP
                if (((Boolean) claim.getFlags().getFlag(CLAIM_FLAG.PVP))) //PvP is allowed
                    return;
            e.setCancelled(true);
            cooldown(damaged);
            cooldown(damager);
        }
    }

    void cooldown(Entity e) {
        if (damageCooldown.containsKey(e))
            Bukkit.getScheduler().cancelTask(damageCooldown.get(e));
        damageCooldown.put(e, Bukkit.getScheduler().scheduleSyncDelayedTask(Pueblos.getInstance(), () -> {
            damageCooldown.remove(e);
        }, 20L * 10));
    }
}
