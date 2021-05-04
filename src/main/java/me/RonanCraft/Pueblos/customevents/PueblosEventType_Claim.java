package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

abstract class PueblosEventType_Claim extends Event {

    private static final HandlerList handler = new HandlerList();

    Claim claim;

    PueblosEventType_Claim(Claim claim) {
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

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handler;
    }

}
