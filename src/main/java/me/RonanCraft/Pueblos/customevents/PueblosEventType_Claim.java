package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.resources.claims.ClaimInfo;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

abstract class PueblosEventType_Claim extends Event {

    private static final HandlerList handler = new HandlerList();

    ClaimInfo claim;

    PueblosEventType_Claim(ClaimInfo claim) {
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

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handler;
    }

}
