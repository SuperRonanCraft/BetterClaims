package me.RonanCraft.BetterClaims.player.events;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.claims.ClaimData;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_FLAG;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

import java.util.HashMap;

public class EventDamage implements ClaimEvents {

    private final HashMap<Entity, Integer> damageCooldown = new HashMap<>();

    //Damage Entity (Mobs and Players)
    void damageEntity(EntityDamageByEntityEvent e) {
        final Entity damager = e.getDamager();
        final Entity damaged = e.getEntity();

        if (damageCooldown.containsKey(damaged)) {
            //cooldown(damaged);
            e.setCancelled(true); //In a cooldown till we check the claims again
            return;
        } else if (damageCooldown.containsKey(damager)) {
            //cooldown(damager);
            e.setCancelled(true); //In a cooldown till we check the claims again
            return;
        }

        ClaimData claimData = getClaimAt(damager.getLocation(), false);
        if (claimData == null)
            claimData = getClaimAt(damaged.getLocation(), false);
        if (claimData != null) { //BUG: if one player is inside a child claim, attacker can hit, defender cannot
            boolean cancel = false;
            //Player vs Player
            if (damaged instanceof Player && damager instanceof Player) {
                if (!((Boolean) claimData.getFlags().getFlag(CLAIM_FLAG.PVP))) //PvP is allowed
                    cancel = true;
            //Is a Friendly Mob
            } else if (damaged instanceof Creature && !(damaged instanceof Monster)) {
                if (damager instanceof Player) {
                    if (!claimData.isMember((Player) damager))
                        cancel = true;
                } else if (damager instanceof Projectile) {
                    //Player vs Player
                    if (damaged instanceof Player) {
                        if (!((Boolean) claimData.getFlags().getFlag(CLAIM_FLAG.PVP))) //PvP is allowed
                            cancel = true;
                    } else {
                        Projectile projectile = (Projectile) damager;
                        if (projectile.getShooter() instanceof Player) {
                            Player projectile_shooter = (Player) projectile.getShooter();
                            if (!claimData.isMember(projectile_shooter))
                                cancel = true;
                        }
                    }
                }
            //Projectiles
            } else if (damager instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                if (projectile.getShooter() instanceof Player) {
                    //Player vs Player (Projectiles)
                    if (damaged instanceof Player) {
                        if (!((Boolean) claimData.getFlags().getFlag(CLAIM_FLAG.PVP))) {
                            cancel = true;
                        }
                    }
                }
            }
            if (cancel) {
                e.setCancelled(true);
                cooldown(damaged);
                cooldown(damager);
            }
        }
    }

    void damageHanging(HangingBreakByEntityEvent e) {
        final Entity damager = e.getRemover();
        final Entity damaged = e.getEntity();

        ClaimData claimData = getClaimAt(damager.getLocation(), false);
        if (claimData == null)
            claimData = getClaimAt(damaged.getLocation(), false);
        if (claimData != null) {
            if (!(damager instanceof Player && claimData.isMember((Player) damager)))
                e.setCancelled(true);
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
