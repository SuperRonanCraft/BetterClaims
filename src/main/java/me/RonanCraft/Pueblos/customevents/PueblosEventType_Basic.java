package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.resources.claims.ClaimInfo;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

abstract class PueblosEventType_Basic extends Event {

    private static final HandlerList handler = new HandlerList();

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
