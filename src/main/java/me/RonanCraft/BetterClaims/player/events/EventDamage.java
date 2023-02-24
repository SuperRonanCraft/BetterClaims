package me.RonanCraft.BetterClaims.player.events;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.claims.ClaimData;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_FLAG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;

public class EventDamage implements PueblosEvents {

    private final HashMap<Entity, Integer> damageCooldown = new HashMap<>();

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

        ClaimData claimData = getClaimAt(damager.getLocation(), false);
        if (claimData == null)
            claimData = getClaimAt(damaged.getLocation(), false);
        if (claimData != null) { //BUG: if one player is inside a child claim, attacker can hit, defender cannot
            if (damaged instanceof Player && damager instanceof Player) { //Player vs Player
                if (((Boolean) claimData.getFlags().getFlag(CLAIM_FLAG.PVP))) //PvP is allowed
                    return;
            } else if (damager instanceof Player && claimData.isMember((Player) damager)) {
                return;
            } else if (damaged instanceof Monster && damaged.getCustomName() == null) //Garbage mob
                return;
            e.setCancelled(true);
            cooldown(damaged);
            cooldown(damager);
        }
    }

    void cooldown(Entity e) {
        if (damageCooldown.containsKey(e))
            Bukkit.getScheduler().cancelTask(damageCooldown.get(e));
        damageCooldown.put(e, Bukkit.getScheduler().scheduleSyncDelayedTask(BetterClaims.getInstance(), () -> {
            damageCooldown.remove(e);
            //Bukkit.getServer().broadcastMessage("Removed " + e.getName());
        }, 20L));
    }
}
