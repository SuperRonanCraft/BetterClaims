package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimInfo;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

abstract class ClaimEvent extends Event implements Cancellable {

    boolean cancelled;
    private static final HandlerList handler = new HandlerList();

    ClaimInfo claim;

    ClaimEvent(ClaimInfo claim) {
        this.claim = claim;
    }

    public ClaimInfo getClaim() {
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

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
