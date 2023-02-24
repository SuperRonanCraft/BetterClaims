package me.RonanCraft.BetterClaims.player.events;

import org.bukkit.event.entity.EntityExplodeEvent;

public class EventExplosion implements PueblosEvents {

    void onExplosion(EntityExplodeEvent e) {
        e.blockList().removeIf(block -> isProtected(block.getLocation()));
    }
}
