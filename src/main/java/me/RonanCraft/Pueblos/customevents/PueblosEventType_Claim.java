package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.claims.ClaimData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

abstract class PueblosEventType_Claim extends Event {

    private static final HandlerList handler = new HandlerList();

    ClaimData claimData;

    PueblosEventType_Claim(ClaimData claimData, boolean async) {
        super(async);
        this.claimData = claimData;
    }

    public ClaimData getClaim() {
        return claimData;
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
