package me.RonanCraft.BetterClaims.customevents;

import me.RonanCraft.BetterClaims.claims.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EventExample extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private final Claim claim;
    private final Player creator;

    /**
     * Broadcasts this new claim that was created
     * @param claim The claim that was just created
     * @param creator The player who created this claim
     */
    public EventExample(Claim claim, Player creator) {
        this.claim = claim;
        this.creator = creator;
    }

    //Required
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getCreator() {
        return creator;
    }

    public Claim getClaim() {
        return claim;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
