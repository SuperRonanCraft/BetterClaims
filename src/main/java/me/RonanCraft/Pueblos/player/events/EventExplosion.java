package me.RonanCraft.Pueblos.player.events;

import org.bukkit.event.entity.EntityExplodeEvent;

public class EventExplosion {

    private final EventListener listener;

    EventExplosion(EventListener listener) {
        this.listener = listener;
    }

    void onExplosion(EntityExplodeEvent e) {
        e.blockList().removeIf(block -> listener.isProtected(block.getLocation()));
    }
}
