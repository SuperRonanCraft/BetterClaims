package me.RonanCraft.Pueblos.player.events.custom;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EventClaimTeleportTo extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private final Claim claim;
    private final Player player;
    private final Location from_loc;

    public EventClaimTeleportTo(Claim claim, Player player, Location from_loc) {
        this.claim = claim;
        this.player = player;
        this.from_loc = from_loc;
    }

    //Required
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Location getFrom_loc() {
        return from_loc;
    }

    public Player getPlayer() {
        return player;
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
