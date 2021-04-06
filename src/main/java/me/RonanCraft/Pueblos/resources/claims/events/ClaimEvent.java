package me.RonanCraft.Pueblos.resources.claims.events;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

abstract class ClaimEvent extends Event {

    private static final HandlerList handler = new HandlerList();

    Claim claim;

    ClaimEvent(Claim claim) {
        this.claim = claim;
    }

    public Claim getClaim() {
        return claim;
    }

    //Default Stuff
    @Override
    public HandlerList getHandlers() {
        return handler;
    }

    public static HandlerList getHandlerList() {
        return handler;
    }
}
