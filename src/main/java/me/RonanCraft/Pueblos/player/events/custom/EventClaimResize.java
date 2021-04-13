package me.RonanCraft.Pueblos.player.events.custom;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EventClaimResize extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private final Claim claim;
    private final Player editor;
    private final Location newLoc_1;
    private final Location newLoc_2;

    /**
     * Broadcasts this new claim that was created
     * @param claim The claim that was just created
     * @param editor The player who is changing the size of this claim
     */
    public EventClaimResize(Claim claim, Player editor, Location loc_1, Location loc_2) {
        this.claim = claim;
        this.editor = editor;
        this.newLoc_1 = loc_1;
        this.newLoc_2 = loc_2;
    }

    //Required
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Location getNewLoc_1() {
        return newLoc_1;
    }

    public Location getNewLoc_2() {
        return newLoc_2;
    }

    public Player getEditor() {
        return editor;
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
