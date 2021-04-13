package me.RonanCraft.Pueblos.player.events.custom;

import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EventMemberLeave extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private final ClaimMember member;

    public EventMemberLeave(ClaimMember member) {
        this.member = member;
    }

    //Required
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public ClaimMember getMember() {
        return member;
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
